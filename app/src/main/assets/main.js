'use strict';

let myCanvas = new Drawr.DrawingCanvas('canvasDiv');

function updateOptions() {
    let options = JSON.parse(Android.getOptions());
    console.log(options);
    myCanvas.updateOptions(options);
}
