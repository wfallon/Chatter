var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var userSchema = new Schema({
	_id: mongoose.Schema.Types.ObjectId,
    username: String,
    password: String,
    isLoggedIn: {type: Boolean, default: false},
    isAdmin: {type: Boolean, default: false},
    industry: String,
    interests: [String]
});

module.exports = mongoose.model('User', userSchema);