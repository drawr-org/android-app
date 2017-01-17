'use strict';

let myCanvas = new Drawr.DrawingCanvas('canvasDiv', JSON.parse(Android.getOptions()));

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
