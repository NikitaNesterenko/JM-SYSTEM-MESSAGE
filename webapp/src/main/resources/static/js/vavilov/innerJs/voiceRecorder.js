// import {
//     ConversationRestPaginationService,
//     UserRestPaginationService,
//     WorkspaceRestPaginationService
// } from "../../rest/entities-rest-pagination";

URL = window.URL || window.webkitURL;

let gumStream; 						//stream from getUserMedia()
let recorder; 						//WebAudioRecorder object
let input; 							//MediaStreamAudioSourceNode  we'll be recording
let encodingType; 					//holds selected encoding for resulting audio (file)
let encodeAfterRecord = true;       //when to encode
let count = 0;						//count of pressing button for start/stop recording

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

// function rndFunc(id) {
//     alert(" " + id);
//     console.log(" " + id);
//
//     this.user_service = new UserRestPaginationService();
//     this.conversation_service = new ConversationRestPaginationService();
//     this.workspace_service = new WorkspaceRestPaginationService();
//
//     const principal = this.user_service.getLoggedUser();
//     this.conversation_service.deleteConversation(id);
//     this.conversation_service.deleteById(id);
//     const conversations = this.conversation_service.getAllConversationsByUserId(principal.id);
//     const workspace_id = this.workspace_service.getChosenWorkspace();
//
//     const direct_messages_container = $("#direct-messages__container_id");
//     direct_messages_container.empty();
//
//     conversations.forEach((conversation, i) => {
//         if (conversation.workspace.id === workspace_id.id) {
//             const conversation_queue_context_container = $('<div class="p-channel_sidebar__channel" ' +
//                 'style="height: min-content; width: 100%;"></div>');
//             conversation_queue_context_container.className = "p-channel_sidebar__channel";
//             if (conversation.openingUser.id === principal.id) {
//                 conversation_queue_context_container.append(messageChat(conversation.associatedUser, conversation.id));
//             } else {
//                 conversation_queue_context_container.append(messageChat(conversation.openingUser, conversation.id));
//             }
//             direct_messages_container.append(conversation_queue_context_container);
//         }
//     });
//
//     function messageChat(user, conversationId) {
//         return `
//             <button class="p-channel_sidebar__name_button" data-user_id="${user.id}">
//                 <i class="p-channel_sidebar__channel_icon_circle pb-0" data-user_id="${user.id}">●</i>
//                 <span class="p-channel_sidebar__name-3" data-user_id="${user.id}">
//                     <span data-user_id="${user.id}">${user.name}</span>
//                 </span>
//             </button>
//             <button class="p-channel_sidebar__close cross">
//                 <i id="deleteDmButton" data-convId="" class="p-channel_sidebar__close__icon" onclick="rndFunc(${conversationId})">✖</i>
//             </button>
//         `;
//     }
// }