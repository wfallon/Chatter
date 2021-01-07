var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var snippetSchema = new Schema({
    _id: mongoose.Schema.Types.ObjectId,
    title: String,
    description: String,
    author: String,
    sourceId: String,
    url: String,
    transcript: String,
    relatedTopics: [String],
    publishedAt: String,
    runTime: {type: Number, default: 0},
    listenCount: {type: Number, default: 0},
    wordCount: Number,
    audio: String
});

module.exports = mongoose.model('Snippet', snippetSchema);