// request message on server
//Calls SimpleServlet to get the "Hello World" message
xhrGet("SimpleServlet?fl=1&sstr=Hi", function(responseText){
	// add to document
	alert(responseText);
	displayMessage(responseText, 'Bot');
//	var mytitle = document.getElementById('chatBox');
//	mytitle.innerHTML = responseText;

}, function(err){
	console.log(err);
});

//utilities
function createXHR(){
	if(typeof XMLHttpRequest != 'undefined'){
		return new XMLHttpRequest();
	}else{
		try{
			return new ActiveXObject('Msxml2.XMLHTTP');
		}catch(e){
			try{
				return new ActiveXObject('Microsoft.XMLHTTP');
			}catch(e){}
		}
	}
	return null;
}

var count=0;
var params = {  // Object for parameters sent to the Watson Conversation service
	    input: '',
	    context: '',
	};
	var watson = 'Bot';
	var user = '';
	var context;  // Very important. Holds all the data for the current point of the chat.

function newEvent(e) {
	// Only check for a return/enter press - Event 13
	
	if (e.which === 13 || e.keyCode === 13) {
        var userInput = document.getElementById('message');
        var text = userInput.value;  // Using text as a recurring variable through functions
        text = text.replace(/(\r\n|\n|\r)/gm, ""); // Remove erroneous characters
        
        // If there is any input then check if this is a claim step
		// Some claim steps are handled in newEvent and others are handled in userMessage
		if (text) {
		//	alert(user);
			// Display the user's text in the chat box and null out input box
            displayMessage(text, user);
            userInput.value = '';
            userMessage(text);
            
        } else {
            // Blank user message. Do nothing.
			console.error("No message.");
            userInput.value = '';

            return false;
        }
    }
	
	/*
    if (code === 13) {

         // Using text as a recurring variable through functions
       
        var mytitle = document.getElementById('chatBox');
	 	mytitle.innerHTML = "enter";	
        // If there is any input then check if this is a claim step
		// Some claim steps are handled in newEvent and others are handled in userMessage
	 	xhrGet("SimpleServlet", function(responseText){
	 		// add to document
	 		var mytitle = document.getElementById('chatBox');
	 		mytitle.innerHTML = responseText;

	 	}, function(err){
	 		console.log(err);
	 	});
		

			// Display the user's text in the chat box and null out input box
            //displayMessage(text, user);
            
            //userMessage(text);
                       
			
    }*/
}

function userMessage(message) {

   
  
     xhrGet("SimpleServlet?fl=0&sstr="+message, function(responseText){
    	// add to document
    	 displayMessage(responseText, watson);
    	//var mytitle = document.getElementById('chatBox');
    	//mytitle.innerHTML = responseText;

    }, function(err){
    	console.log(err);
    });

    xhr.send();
    
}

/**
 * @summary Display Chat Bubble.
 *
 * Formats the chat bubble element based on if the message is from the user or from Bot.
 *
 * @function displayMessage
 * @param {String} text - Text to be dispalyed in chat box.
 * @param {String} user - Denotes if the message is from Bot or the user. 
 * @return null
 */
function displayMessage(text, user) {
//alert("indisplay");
    var chat = document.getElementById('chatBox');
    var bubble = document.createElement('div');
    bubble.className = 'message';  // Wrap the text first in a message class for common formatting

    // Set chat bubble color and position based on the user parameter
	if (user === watson) {
        bubble.innerHTML = "<div class='bot'>" + text + "</div>";
    } else {
    	//alert("else");
        bubble.innerHTML = "<div class='user'>" + text + "</div>";
    }

    chat.appendChild(bubble);
    chat.scrollTop = chat.scrollHeight;  // Move chat down to the last message displayed
    document.getElementById('message').focus();

    return null;
}

function xhrGet(url, callback, errback){
	var xhr = new createXHR(); //create object
	xhr.open("GET", url, true); //resource get. true is for asynchronous call
	xhr.onreadystatechange = function(){
		if(xhr.readyState == 4){
			if(xhr.status == 200){
				callback(xhr.responseText);
			}else{
				errback('service not available');
			}
		}
	};
	xhr.timeout = 3000;
	xhr.ontimeout = errback;
	xhr.send();
}
function parseJson(str){
	return window.JSON ? JSON.parse(str) : eval('(' + str + ')');
}
function prettyJson(str){
	// If browser does not have JSON utilities, just print the raw string value.
	return window.JSON ? JSON.stringify(JSON.parse(str), null, '  ') : str;
}