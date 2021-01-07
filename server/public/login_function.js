var btn = document.getElementById("submit");

btn.addEventListener("click", function() {
	var uname = document.getElementById("name").value;
	var pw = document.getElementById("pw").value;
	var body = {username : uname};

	var userRequest = new XMLHttpRequest();

	userRequest.open('GET', "http://localhost:3000/userByUsername/" + uname);

	userRequest.onload = function () {
		var appended = document.getElementById('body');

		if (userRequest.readyState == 4 && userRequest.status == 200) {
			var user = JSON.parse(userRequest.responseText);

			if (pw == user.password) {
				window.open('databaseoperations.html', '_self');
			} else {
				var edited = document.createTextNode('incorrect password <br />');
				appended.appendChild(edited);
			}
			//log in etc-- navigate to new page
		} else {
			var edited = document.createTextNode('user not found <br />');
			appended.appendChild(edited)
		}
	};
	userRequest.send();
});
