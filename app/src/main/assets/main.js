let myCanvas = new Drawr.DrawingCanvas('canvasDiv', JSON.parse(Android.getOptions()));
let server;
function updateOptions() {
    let options = JSON.parse(Android.getOptions());
    console.log(options);
    myCanvas.updateOptions(options);
}

function clearCanvas() {
    console.log('updateUptions');
    myCanvas.clearCanvas(true);
}

function undo() {
    console.log('undo');
    myCanvas.undoLastClick();
}

function connectAndJoinSession(username, hostUrl, sessionId) {
    connectToServer(username, hostUrl);
    joinSession(sessionId);
}

function connectAndNewSession(username, hostUrl) {
    connectToServer(username, hostUrl);
    newSession();
}

function connectToServer(username, hostUrl) {
    try {
        server = new Drawr.ServerConnection({
            name: username,
            id: '0'
        }, hostUrl);
    } catch (e) {
        console.log(e);
    }
    server.addEventListener('update-canvas', function(data) {
        if (server._user.name !== data.username) {
            canvas.remoteUpdate(JSON.parse(data.canvasState));
        }
    }, myCanvas);

    myCanvas.addEventListener('new-click', function(clicks) {
        server.sendCanvasUpdate(clicks);
    });
}

function joinSession(sessionId) {
    console.log('joinSession');
    server.joinSession(sessionId, Android.joinServerCallback());
}

function newSession() {
    console.log('newSession');
    server.newSession('Android', Android.newSessionCallback());
}
