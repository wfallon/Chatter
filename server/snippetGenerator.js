const NewsAPI = require('newsapi');
var mongoose = require('mongoose');
const Snippet = require('./api/models/snippet.js');
const textToSpeech = require('@google-cloud/text-to-speech');
const news_API_KEY = "9e21f9a62e204729b1d7706337419823";
const newsapi = new NewsAPI(news_API_KEY);
const apiDomain = "http://localhost:3000";
let currentDate = new Date();
let monthAgoDate = new Date();
var oneMonthInMillis = 2628000000;
monthAgoDate.setUTCMilliseconds(currentDate.getUTCMilliseconds() - oneMonthInMillis);
let today = currentDate.toISOString().substring(0, 10);
let oneMonthAgo = monthAgoDate.toISOString().substring(0, 10);
// Import other required libraries
const fs = require('fs');
const util = require('util');

// Creates a client
const googleClient = new textToSpeech.TextToSpeechClient();
// const Gnews = require('node-gnews').Gnews;
// const instance = new Gnews();
 
// // Headlines for topics
// instance.headlines().then(articles => console.log(articles));


const connectionString = 'mongodb+srv://jroseman:Poland33@chatter0-tmsbt.mongodb.net/test';
const connectionOptions = {
	useUnifiedTopology: true,
	useNewUrlParser: true
};


async function generateAudio(text, filename, voice, lang, credentials) {
  // Construct the request
  const request = {
    input: {text: text},
    // Select the language and SSML voice gender (optional)
    voice: {languageCode: lang, ssmlGender: voice},
    // select the type of audio encoding
    audioConfig: {audioEncoding: 'MP3'},
  };

  // Performs the text-to-speech request
  const [response] = await googleClient.synthesizeSpeech(request);
  // Write the binary audio content to a local file
  const writeFile = util.promisify(fs.writeFile);
  await writeFile(`./audio/${filename}.mp3`, response.audioContent, 'binary');
  console.log(`Audio content written to file: ${filename}.mp3`);
}

//source categories:'technology', 'business', 'entertainment', 'health', 'science', 'sports'
const categories = ['technology', 'business', 'entertainment', 'health', 'science', 'sports'];
async function getSourcesUS(category) {
  return newsapi.v2.sources({
      category: category,
      language: 'en',
      country: 'us'
    }).then(response => {
      if (response.status == "ok") {
        return response.sources;
      } else {
        return [];
      }
    });
}

async function getArticles(query, dateFrom, dateTo, sources) {
  //dateFrom or dateTo for example may be '2017-12-01'
  //sources - A comma-seperated string of identifiers (maximum 20) for the news sources or blogs you want headlines from.
  //see documentation at https://newsapi.org/docs/endpoints/everything
  return newsapi.v2.everything({
    q: query,
    sources: sources.join(),
    from: dateFrom,
    to: dateTo,
    language: 'en',
    sortBy: 'relevancy'
  }).then(response => {
    if (response.status == "ok") {
      return response.articles;
    } else {
      return [];
    }
  });
}

module.exports.generateNewSnippets =  async function(n, query, category, credentials) {
  return getSourcesUS(category)
  .then(response => {
    var sources = [];
    response.forEach(src => {
      sources.push(src.id);
    });
    getArticles(query, oneMonthAgo, today, sources).then(async (articles) => {
      for (var i = 0; i < Math.min(articles.length, n); i++) {
        if (articles[i].content) {
          const id = new mongoose.Types.ObjectId();
          var snippet = {
            _id: id,
            title: articles[i].title,
            description: articles[i].description,
            author: articles[i].author,
            publishedAt: articles[i].publishedAt.substring(0, 10),
            sourceId: articles[i].source.id,
            sourceLink: articles[i].url,
            transcript: articles[i].content,
            wordCount: articles[i].content.split(" ").length,
            relatedTopics: [category],
            audio: `${apiDomain}/server/audio/${id}.mp3`
          }
          var voice = Math.random() < 0.5 ? 'MALE' : 'FEMALE';
          var lang = Math.random() < 0.5 ? 'en-US' : 'en-GB';
          generateAudio(snippet.transcript, id, voice, lang, credentials);
          new Snippet(snippet).save();
        }
      }
    })
  }).catch(err => {return "BAD"});
}


//generateNewSnippets(1, 'coronavirus', 'health').catch(err => console.log(err));

//source categories:'technology', 'business', 'entertainment', 'health', 'science', 'sports'
// mongoose.connect(connectionString, connectionOptions)
// .then(async () => {
//   console.log("Connected to MongoDB");
//   console.log("Fetching news articles and converting them to audio...");
//   await generateNewSnippets(20, 'startups OR real estate', 'technology');
// }).finally(async () => {
//   console.log("Disconnected from MongoDB");
//   //mongoose.disconnect()
// });

//FOR JACK: export GOOGLE_APPLICATION_CREDENTIALS="/Users/jackroseman/Desktop/DEV/350S20-10/server/GCP/google_service_key.json"