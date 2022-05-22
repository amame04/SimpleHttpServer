window.onload = function() {
    timerID = setInterval('clock()',500);
};

function clock() {
	document.getElementById("view_clock").innerHTML = getNow();
}

function getNow() {
	var now = new Date();
	var year = now.getFullYear();
	var mon = now.getMonth()+1;
	var day = now.getDate();
	var hour = now.getHours();
	var min = now.getMinutes();
	var sec = now.getSeconds();

	var s = year + " " + mon + "/" + day + " " + hour + ":" + min + ":" + sec;
	return s;
}