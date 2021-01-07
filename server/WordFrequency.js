const TreeMap = require("treemap-js");

class WordFrequency {

  //@param String str: text from article
  constructor(str) {
    this.text = str.toLowerCase();
    this.map = new TreeMap();
    this.populateMap();
  }

  //populates the TreeMap with the words in the string input
  populateMap() {
    //remove punctuation
    var punctuationless = this.text.replace(/[^\w\s]|_/g, "")
         .replace(/\s+/g, " ");
    //breakup into individual words
    var words = punctuationless.split(" ");

    for (var i = 0; i < words.length; i++) {
      if (this.map.get(words[i]) == null) {
        this.map.set(words[i], 1);
      } else {
        this.map.set(words[i], this.map.get(words[i]) + 1);
      }
    }
  }

  //returns how many times a word appears
  getFrequency(word){
    var lowercase = word.toLowerCase();
    return this.map.get(lowercase) ? this.map.get(lowercase) : 0;
  }


  getUniqueWords() {
    return this.map.getLength();
  }

  //returns the most common word and its frequency in JSON format
  getMostFrequentWord() {
    if(this.map.getLength() == 0) {
      return 0;
    }
    var max = 0;
    var maxKey = "";
    this.map.each(function (value, key) {
      if (value > max) {
        max = value;
        maxKey = key;
      }
    });
    var json = '{' +
    '"word": \"' + maxKey + "\", " +
    '"frequency": \"' + max + "\"" +
    '}';
    return JSON.parse(json);
  }

  //returns the n most frequent words in JSON Array format
  getNMostFrequentWords(n) {
    var words = this._getFrequentWordsInternal([], this.map.getTree());
    words.sort(function (a, b) {
      if (a["frequency"] < b["frequency"]) {
        return 1;
      }
      if (a["frequency"] > b["frequency"]) {
        return -1;
      }
      return 0;
    });
    return words.slice(0, n);
  }

  _getFrequentWordsInternal(arr, node) {
    if(node == null) {
      return arr;
    } else {
      var json = '{' +
        '"word": \"' + node.key + "\", " +
        '"frequency": \"' + node.value + "\"" +
      '}';
      var left = this._getFrequentWordsInternal([], node.left);
      left.push(JSON.parse(json));
      var right = this._getFrequentWordsInternal([], node.right);
      return left.concat(right);
    }
  }

  //returns all words and their frequencies in JSON Array format
  getAllWordData() {
    return this.getNMostFrequentWords(this.map.getLength());
  }
}
