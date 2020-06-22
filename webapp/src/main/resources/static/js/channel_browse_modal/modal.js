import { ownFetch } from '/static/js/rest/fetch-service.js';
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

// todo дубль?
async function getAllChannels () {
    const response = await ownFetch.get( "/rest/api/channels/all" );
    const data = await response.json();
    htmlChannelList( data );
}

async function getPrivate () {
    const response = await ownFetch.get("rest/api/channels/private");
    const data = await response.json();
    htmlChannelList(data);
}

async function getArchive () {
    const response = await ownFetch.get("/rest/api/channels/all_archive");
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
                       <p>Created  by <strong>${data[key].username}</strong> on <strong>${data[key].createdDate}</strong></p>
                       </div>
                       <div class="col-lg-2">
                       <button id="button-channel-archive" type="button" class="btn btn-primary" data-row-id="${data[key].id}" >Click</button>
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

// todo дубль?
async function getAll () {
    const response = await ownFetch.get( "/rest/api/channels/all" );
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
                       <p>#${data[key].name}</p>
                       <p>Created  by <strong>${data[ key ].username}</strong> on <strong>${data[ key ].createdDate}</strong></p>
                       </div>
                       <div class="col-lg-2">
                       <button id="button-channel1" type="button" class="btn btn-primary">Click</button>
                       </div>
                       </div>`;
            }
        } );
        $( ".channel-list #ch-list" ).remove();
        $( ".channel-list" ).append( html );
    } );
}

$( function () {
    let button = document.getElementById( "button-channel1" );
    button.onclick = function unzipChannel()  {
        const response = ownFetch.post(`/rest/api/channels/archiving/${id}`, {
        });
        return response.json();
    };
});


$(function () {
    $(document).on('click touchstart', '#button-channel-archive', function () {
        let id = $(this).attr("data-row-id")
        $('#channelModal').modal('toggle');
        ownFetch.get(`/rest/api/channels/block/${id}`, {
        });
        location.reload();
    });
});
$(function () {
    $(document).on('click touchstart', '#restoreArchiveChannel', function () {
        let id = channel_id;
        ownFetch.get(`/rest/api/channels/unzip/${id}`, {
        });
        location.reload();
    });
});

$(function () {
    $(document).on('click touchstart', '#closeArchiveChannel', function () {
        let id = channel_id;
        ownFetch.get(`/rest/api/channels/block/${id}`, {
        });
        location.reload();
    });
})
