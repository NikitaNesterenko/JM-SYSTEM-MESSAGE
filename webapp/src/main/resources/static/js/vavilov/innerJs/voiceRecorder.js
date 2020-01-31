//webkitURL is deprecated but nevertheless
URL = window.URL || window.webkitURL;

let gumStream; 						//stream from getUserMedia()
let recorder; 						//WebAudioRecorder object
let input; 							//MediaStreamAudioSourceNode  we'll be recording
let encodingType; 					//holds selected encoding for resulting audio (file)
let encodeAfterRecord = true;       //when to encode
let count = 0;						//count of pressing button for start/stop recording

// shim for AudioContext when it's not avb. 
let AudioContext = window.AudioContext || window.webkitAudioContext;
let audioContext; //new audio context to help us record

function recording() {
    __log("recording() called");

    let clicked = true;
    if (clicked) {
        $("voiceMessageBtn").css('color', 'red !important');
        clicked = false;
    } else {
        $("voiceMessageBtn").css('color', 'blue');
        clicked = true;
    }


    if (count % 2 == 0) { //wtf js
        $('#inputMe').html("");
        let constraints = {audio: true, video: false};
        navigator.mediaDevices.getUserMedia(constraints).then(function (stream) {
            __log("getUserMedia() success, stream created, initializing WebAudioRecorder...");

            audioContext = new AudioContext();

            //assign to gumStream for later use
            gumStream = stream;

            /* use the stream */
            input = audioContext.createMediaStreamSource(stream);

            //stop the input from playing back through the speakers
            //input.connect(audioContext.destination)

            //get the encoding
            encodingType = "mp3";

            recorder = new WebAudioRecorder(input, {
                workerDir: "js/vavilov/innerJs/", // must end with slash
                encoding: encodingType,
                numChannels: 2, //2 is the default, mp3 encoding supports only 2
                onEncoderLoading: function (recorder, encoding) {
                    // show "loading encoder..." display
                    __log("Loading " + encoding + " encoder...");
                },
                onEncoderLoaded: function (recorder, encoding) {
                    // hide "loading encoder..." display
                    __log(encoding + " encoder loaded");
                }
            });

            recorder.onComplete = function (recorder, blob) {
                __log("Encoding complete");
                createDownloadLink(blob, recorder.encoding);
            };

            recorder.setOptions({
                timeLimit: 15,
                encodeAfterRecord: encodeAfterRecord,
                ogg: {quality: 0.5},
                mp3: {bitRate: 160}
            });

            //start the recording process
            recorder.startRecording();
            count++;
            __log("Recording started");

        }).catch(function (err) {
            count++;
            console.log("Error while recording")
        });
    } else {
        console.log("stopRecording() called");

        //stop microphone access
        gumStream.getAudioTracks()[0].stop();

        recorder.finishRecording();
        count++;
        __log('Recording stopped');
    }
}

function createDownloadLink(blob, encoding) {

    let url = URL.createObjectURL(blob);
    let au = document.createElement('audio');
    let li = document.createElement('li');
    let link = document.createElement('a');

    //add controls to the <audio> element
    au.controls = true;
    au.src = url;
    au.id = "audioInput";

    //link the a element to the blob
    link.href = url;
    link.download = new Date().toISOString() + '.' + encoding;
    link.innerHTML = link.download;

    //add the new audio and a elements to the li element
    li.appendChild(au);
    li.appendChild(link);

    //add the li element to the ordered list
    // vm_selector.appendChild(au);
    inputMe.appendChild(au);

    let input = document.querySelector("#form_message_input");

	// input.value = au;
	// $("#form_message_input").focus();
}

//helper function
function __log(e, data) {
    console.log("\n" + e + " " + (data || ''));
}