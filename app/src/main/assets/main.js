'use strict';

let myCanvas = new Drawr.DrawingCanvas('canvasDiv', JSON.parse(Android.getOptions()));

function updateOptions() {
    let options = JSON.parse(Android.getOptions());
    console.log(options);
    myCanvas.updateOptions(options);
}

function clearCanvas() {
    myCanvas.clearCanvas(true);
}
