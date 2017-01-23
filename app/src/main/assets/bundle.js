'use strict';

var canvas = new drawr.DrawrCanvas('canvasDiv', JSON.parse(Android.getOptions()));
var client = void 0;
function updateOptions() {
    var options = JSON.parse(Android.getOptions());
    console.log(options);
    canvas.updateOptions(options);
}

function clearCanvas() {
    console.log('updateUptions');
    canvas.reset();
}

function undo() {
    console.log('undo');
    canvas.undoLastClick();
}

function connectAndJoinSession(username, host, port, sessionId) {
    var options = {
        host: host,
        port: port
    };
    initClient(username, options);
    joinSession(sessionId);
}

function connectAndNewSession(username, host, port) {
    var options = {
        host: host,
        port: port
    };
    initClient(username, options);
    newSession();
}

function initClient(username, options) {
    client = new drawr.DrawrClient({
        name: username,
        id: '0'
    }, options);
    client.addEventListener('update-canvas', function (data) {
        if (client._user.name !== data.username) {
            canvas.remoteUpdate(JSON.parse(data.canvasState));
        }
    });
    client.addEventListener('server-down', function (data) {
        console.log('server-down');
        Android.serverShutdown();
    });

    canvas.addEventListener('new-click', function (clicks) {
        client.sendCanvasUpdate(clicks);
    });
}

function joinSession(sessionId) {
    console.log('joinSession');
    client.joinSession(sessionId).then(function () {
        Android.joinSessionCallback('true');
    }).catch(function () {
        Android.joinSessionCallback('false');
    });
}

function newSession() {
    console.log('newSession');
    client.newSession('Android').then(function (id) {
        Android.newSessionCallback('true', id);
    }).catch(function (err) {
        console.log(err);
        Android.newSessionCallback('false', '');
    });
}
