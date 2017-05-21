import drawr from 'drawr';
console.log(drawr)
let canvas = new drawr.DrawrCanvas('canvasDiv', JSON.parse(Android.getOptions()));
let client;
function updateOptions() {
    let options = JSON.parse(Android.getOptions());
    console.log(options);
    canvas.updateOptions(options);
}
export {updateOptions};

function clearCanvas() {
    console.log('updateUptions');
    canvas.reset();
}
export {clearCanvas};

function undo() {
    console.log('undo');
    canvas.undoLastClick();
}
export {undo};

function connectAndJoinSession(username, host, port, sessionId) {
    let options = {
        host: host,
        port: port
    };
    initClient(username, options);
    joinSession(sessionId);
}
export {connectAndJoinSession};

function connectAndNewSession(username, host, port) {
    let options = {
        host: host,
        port: port
    };
    initClient(username, options);
    newSession();
}
export {connectAndNewSession};

function initClient(username, options) {
    client = new drawr.DrawrClient({
        name: username,
        id: '0'
    }, options);
    client.addEventListener('update-canvas', function(data) {
        if (client._user.name !== data.username) {
            canvas.remoteUpdate(JSON.parse(data.canvasState));
        }
    });
    client.addEventListener('server-down', function(data) {
        console.log('server-down');
        Android.serverShutdown();
    });

    canvas.addEventListener('new-click', function(clicks) {
        client.sendCanvasUpdate(clicks);
    });
}
export {initClient};

function joinSession(sessionId) {
    console.log('joinSession');
    client.joinSession(sessionId)
        .then(() => {
            Android.joinSessionCallback('true');
        })
        .catch(() => {
            Android.joinSessionCallback('false');
        });
}
export {joinSession};

function newSession() {
    console.log('newSession');
    client.newSession('Android')
        .then(id => {
            Android.newSessionCallback('true', id);
        })
        .catch(err => {
            console.log(err);
            Android.newSessionCallback('false', '');
        });
}
export {newSession};