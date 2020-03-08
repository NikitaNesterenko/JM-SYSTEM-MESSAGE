$(function () {
    getAllChannels();
    selectChannelList();
});

$( function () {
    let button = document.getElementById( "btnChannel-info_1" );
    button.onclick = function () {
        $( '#channelModal' ).modal();
    };
});

async function getAllChannels () {
    const response = await fetch( "/rest/api/channels/all" );
    const data = await response.json();
    htmlChannelList( data );
}

async function getPrivate () {
    const response = await fetch("rest/api/channels/private");
    const data = await response.json();
    htmlChannelList(data);
}

async function getArchive () {
    const response = await fetch("/rest/api/channels/all_archive");
    const data = await response.json();
    htmlChannelList(data);
}

function selectChannelList () {
    let select = document.querySelector( 'select' );

    select.addEventListener( 'change', function () {
        switch (this.value) {
            case 'all' :
                return getAllChannels();
            case 'private' :
                return getPrivate();
            case 'archive' :
                return getArchive();
        }
    } );
}

function htmlChannelList (data) {
    let html = "";

    $.each(data, function (key, value) {

        html += `<div class="channel-list row align-items-center" id="ch-list"><hr>
                       <div class="col-lg-10 align-items-center">
                       <div hidden id="channel_id">${data[key].id}</div>
                       <p><b>#${data[key].name}</b></p>
                       <p>Created  by <strong>${data[key].userName}</strong> on <strong>${data[key].createdDate}</strong></p>
                       </div>
                       <div class="col-lg-2">
                       <button id="button-channel" type="button" class="btn btn-primary" onclick="clickButton(this)">Click</button>
                       </div>
                       </div>`;
    });
    $( ".channel-list #ch-list" ).remove();
    $( ".channel-list" ).append( html);
}


// filter channel

$(function () {
    getAll();
});

async function getAll () {
    const response = await fetch( "/rest/api/channels/all" );
    const data = await response.json();
    selectCh (data);
}
let idChannel = " " ;

function selectCh (data) {

    $( '#input-in' ).keyup( function () {
        let searchfild = $( '#input-in' ).val();
        let expression = new RegExp( searchfild, "i" );
        let html = ' ';
        $.each( data, function (key, value) {
            idChannel = data[key].id;

            if (value.name.search( expression ) != -1) {

                html += `<div class="channel-list row align-items-center" id="ch-list"><hr>
                       <div class="col-lg-10 align-items-center">
                       <div hidden id="channel_id">${idChannel}</div>
                       <p>#${data[key].name}</p>
                       <p>Created  by <strong>${data[ key ].userName}</strong> on <strong>${data[ key ].createdDate}</strong></p>
                       </div>
                       <div class="col-lg-2">
                       <button id="button-channel1" type="button" class="btn btn-primary" onclick="unzipChannel()">Click</button>
                       </div>
                       </div>`;
            }
        } );
        $( ".channel-list #ch-list" ).remove();
        $( ".channel-list" ).append( html );
    } );
}

// $('#button-channel1').on('click', unzip());
//
// async function unzip () {
//     let response = await fetch( `/rest/api/channels/unzip/${idChannel}`, {
//         method : 'POST',
//         headers : {
//             'Content-Type' : 'application/json'
//         }
//     });
//     let json = await response.json();
//     console.log(json);
// }