let canvas = new Drawr.DrawingCanvas('canvasDiv', JSON.parse(Android.getOptions()));
let client;
function updateOptions() {
    let options = JSON.parse(Android.getOptions());
    console.log(options);
    canvas.updateOptions(options);
}

function clearCanvas() {
    console.log('updateUptions');
    canvas.clearCanvas(true);
}

function undo() {
    console.log('undo');
    canvas.undoLastClick();
}

function connectAndJoinSession(username, host, port) {
    let options = {
        host: host,
        port: port
    };
    connectToServer(username, options);
    joinSession(sessionId);
}

function connectAndNewSession(username, host, port) {
    let options = {
        host: host,
        port: port
    };
    connectToServer(username, options);
    newSession();
}

function connectToServer(username, options) {
    client = new Drawr.ServerConnection({
        name: username,
        id: '0'
    }, options);
    client.addEventListener('update-canvas', function(data) {
        if (client._user.name !== data.username) {
            canvas.remoteUpdate(JSON.parse(data.canvasState));
        }
    });

    canvas.addEventListener('new-click', function(clicks) {
        client.sendCanvasUpdate(clicks);
    });
}

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

function newSession() {
    console.log('newSession');
    client.newSession('Android')
        .then(id => {
            Android.newSessionCallback('true', id);
        })
        .catch(err => {
            Android.newSessionCallback('false');
        });
}
