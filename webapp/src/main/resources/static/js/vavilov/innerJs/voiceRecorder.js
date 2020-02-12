URL = window.URL || window.webkitURL;

let gumStream; 						//stream from getUserMedia()
let recorder; 						//WebAudioRecorder object
let input; 							//MediaStreamAudioSourceNode  we'll be recording
let encodingType; 					//holds selected encoding for resulting audio (file)
let encodeAfterRecord = true;       //when to encode
let count = 0;						//count of pressing button for start/stop recording

// shim for AudioContext when it's not avb.
let AudioContext = window.AudioContext || window.webkitAudioContext;
let audioContext;

function recording() {
    if (count % 2 === 0) {
        document.getElementById("voiceMessageBtn").style.color="red";
        $('#inputMe').html("");

        let constraints = {audio: true, video: false};
        navigator.mediaDevices.getUserMedia(constraints).then(function (stream) {
            console.log("getUserMedia() success, stream created, initializing WebAudioRecorder...");

            audioContext = new AudioContext();
            gumStream = stream;

            input = audioContext.createMediaStreamSource(stream);

            //stop the input from playing back through the speakers
            //input.connect(audioContext.destination)

            encodingType = "mp3";

            recorder = new WebAudioRecorder(input, {
                workerDir: "js/vavilov/innerJs/", // must end with slash
                encoding: encodingType,
                numChannels: 2, //2 is the default, mp3 encoding supports only 2
                onEncoderLoading: function (recorder, encoding) {
                    console.log("Loading " + encoding + " encoder...");
                },
                onEncoderLoaded: function (recorder, encoding) {
                    console.log(encoding + " encoder loaded");
                }
            });

            recorder.onComplete = function (recorder, blob) {
                console.log("Encoding complete");
                buildAudio(blob);
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
            console.log("Recording started");

        }).catch(function (err) {
            count++;
            console.log("Error while recording")
        });
    } else {
        document.getElementById("voiceMessageBtn").style.color="#646f83";
        console.log("stopRecording() called");

        //stop microphone access
        gumStream.getAudioTracks()[0].stop();

        recorder.finishRecording();
        count++;
        console.log('Recording stopped');
    }
}

function buildAudio(blob) {

    let url = URL.createObjectURL(blob);
    let au = document.createElement('audio');

    au.controls = true;
    au.src = url;
    au.id = "audioInput";

    inputMe.appendChild(au);
}

function rndFunc(id) {
    alert("1" + id);
    console.log("1" + id);
}