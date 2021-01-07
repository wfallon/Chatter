// set up Express
var express = require('express');
var app = express();
var mongoose = require('mongoose');
var snippetGenerator = require('./snippetGenerator.js')
const User = require('./api/models/user.js');
const Snippet = require('./api/models/snippet.js');

const { google } = require('googleapis');
const OAuth2Data = require('./GCP/google_key.json');
const CLIENT_ID = OAuth2Data.web.client_id;
const CLIENT_SECRET = OAuth2Data.web.client_secret;
const REDIRECT_URL = OAuth2Data.web.redirect_uris;

const oAuth2Client = new google.auth.OAuth2(CLIENT_ID, CLIENT_SECRET, REDIRECT_URL);
var authed = false;

const connectionString = 'mongodb+srv://jroseman:Poland33@chatter0-tmsbt.mongodb.net/test';
const connectionOptions = {
	useUnifiedTopology: true,
	useNewUrlParser: true
}

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
// set up EJS
app.set('view engine', 'ejs');
app.use('/public', express.static('public'));


app.get('/', (req, res) => {
    if (!authed) {
        // Generate an OAuth URL and redirect there
        const url = oAuth2Client.generateAuthUrl({
            access_type: 'offline',
            scope: 'https://www.googleapis.com/auth/gmail.readonly'
        });
        console.log(url)
        res.redirect(url);
    } else {
        const gmail = google.gmail({ version: 'v1', auth: oAuth2Client });
        gmail.users.labels.list({
            userId: 'me',
        }, (err, res) => {
            if (err) return console.log('The API returned an error: ' + err);
            const labels = res.data.labels;
            if (labels.length) {
                console.log('Labels:');
                labels.forEach((label) => {
                    console.log(`- ${label.name}`);
                });
            } else {
                console.log('No labels found.');
            }
        });
        res.redirect('/public/databaseoperations.html');
    }
})

app.get('/auth/google/callback', function (req, res) {
    const code = req.query.code
    if (code) {
        // Get an access token based on our OAuth code
        oAuth2Client.getToken(code, function (err, tokens) {
            if (err) {
                console.log('Error authenticating')
                console.log(err);
            } else {
                console.log('Successfully authenticated');
                oAuth2Client.setCredentials(tokens);
                authed = true;
                res.redirect('/')
            }
        });
    }
});

app.post('/addUser', (req, res) => {//add a new user to the database with this endpoint
	var user = new User({
		_id: new mongoose.Types.ObjectId(),
		username: req.body.username,
		password: req.body.password,
		isLoggedIn: req.body.isLoggedIn,
		isAdmin: req.body.isAdmin,
		industry: req.body.industry,
		interests: req.body.interests
	})
	//check if username is taken
	User.findOne({username: req.body.username}).exec().then((userFound) => {
		if (userFound) { //username already added
			res.status(403).json({ //Return status 403 Forbidden because username already exists, return userprofile
				message: "Username already exists",
				userProfile: userFound
			});
		} else { //add user, return status 201 and the user profile that was added
			user.save().then(result => {
				res.status(201).json({
					message: "POST request to /addUser",
					createdUser: user
				});
			})
		}
		console.log(user);
	});
});

app.get('/users', (req, res) => {
	User.find(req.body).exec().then(users => {
		console.log(users);
		if (users.length != 0) {
			console.log(users);
			res.status(200).json(users);
		} else {
			console.log("No user found");
			res.status(404).json({
				message: "No user found"
			})
		}
	}).catch(err => {
		console.log(err);
	})
});

//search for users with fields matching those in the request body
app.get('/userByUsername/:username', (req, res) => { //Ask Jack for an example
	User.findOne({username: req.params.username}).exec().then(user => {
		if (user) {
			console.log(user);
			res.status(200).json(user);
		} else {
			res.status(404).json({
				message: "No user found"
			})
		}
	}).catch(err => {
		res.json({message: "No user found"});
		console.log(err);
	})
});

//get a single user using id, otherwise return a status 404 not found, ask Jack for an example
app.get('/userById/:userId', (req, res) => {//get user by id
	User.findById(req.params.userId).exec().then(user => {
		if (user){
			console.log(user);
			res.status(200).json(user);
		} else {
			res.status(404).json({
				message: "No user found"
			})
		}
	}).catch(err => {
		res.json({message: "No user found"});
		console.log(err);
	})
});

//Ask Jack for an example
app.get('/snippets', (req, res) => {
	Snippet.find(req.body).exec().then(snippets => {
		if (snippets.length != 0) {
			console.log(snippets);
			res.status(200).json(snippets);
		} else {
			res.status(404).json({
				message: "No snippets found"
			})
		}
	}).catch(err => {
		console.log(err);
	})
});

//Get snippet by Id
app.get('/snippetById/:snipId', (req, res) => {//get user by id
	Snippet.findById(req.params.snipId).exec().then(snippet => {
		if (snippet){
			console.log(snippet);
			res.status(200).json(snippet);
		} else {
			res.status(404).json({
				message: "No snippet found"
			})
		}
	}).catch(err => {
		res.json({message: "No snippet found"});
		console.log(err);
	})
});

app.get('/getAudioById/:audioId', (req, res) => {
	res.sendFile(__dirname + '/audio/' + req.params.audioId + '.mp3');
});

app.post('/addListen/:snippetId', (req, res) => {//increment listen count of a snippet
	Snippet.updateOne({_id:req.params.snippetId}, {$inc: {listenCount : 1}}).exec().then(response => {
		if (response) {
			console.log(response);
			res.status(200).json({message: "Added a listen to this snippet"});
		} else {
			res.status(404).json({
				message: "No snippet found"
			})
		}
	}).catch(err => {
		res.json({message: "No snippet found"});
		console.log(err);
	})
});

app.post('/addSnippet', (req, res) => {//add a new user to the database with this endpoint
	const snippet = new Snippet({
		_id: new mongoose.Types.ObjectId(),
		title: req.body.title,
    	listenCount: req.body.listenCount,
    	wordCount: req.body.wordCount,
    	source: req.body.source,
    	sourceLink: req.body.sourceLink,
    	transcript: req.body.transcript,
    	runTime: req.body.runTime,
		relatedTopics: req.body.relatedTopics
	});

	snippet.save().then(result => {
		console.log(result);
	}).catch(err => {
		console.log(err);
	});
	res.status(201).json({
		message: "POST request to /addSnippet",
		createdSnippet: snippet
	});
});

app.post('/generateSnippets/:category/:query/:n', async (req, res) => {
	var message = await snippetGenerator.generateNewSnippets(req.params.n, req.params.query, req.params.category, oAuth2Client.credentials);
	console.log(message);
	if (message == "BAD") {
		res.status(409).json({message:"BAD"});
	} else {
		res.status(200).json({message:message});
	}
});

app.post('/clearSnippets', (req, res) => {
	Snippet.deleteMany().exec().catch(err => console.log(err));
	console.log(`Deleting snippet ${req.params.id}`);
	res.status(200).json({message:"Deleted snippet"});
});

app.post('/deleteSnippet/:id', (req, res) => {
	Snippet.findByIdAndDelete(req.params.id).exec().catch(err => console.log(err));
	console.log(`Deleting snippet ${req.params.id}`);
	res.status(200).json({message:"Deleted snippet"});
});

// This starts the web server on port 3000.
app.listen(3000, () => {
	console.log('Listening on port 3000');
	mongoose.connect(connectionString, connectionOptions)
	.then(console.log("Server connected to MongoDB"))
	.catch(err => console.log(err));
});
