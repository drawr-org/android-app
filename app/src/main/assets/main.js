'use strict';

Drawr.DrawingCanvas.prototype.clearCanvas = function() {
    this._context.restore();
    this._context.clearRect(0, 0, this._width, this._height);
    this._context.save();
    this._context.scale(this._scaleX, this._scaleY);
}
let myCanvas = new Drawr.DrawingCanvas('canvasDiv');

function updateOptions() {
    let options = JSON.parse(Android.getOptions());
    console.log(options);
    myCanvas.updateOptions(options);
}
