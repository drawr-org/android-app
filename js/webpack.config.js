module.exports = {
    "entry": __dirname+"/main.js",
    "output": {
        "path": __dirname + "/../app/src/main/assets",
        "filename": "bundle.js",
        "libraryTarget": "window"
    }/*,
    module:{
        loaders:[{ test: /\.css$/, loader: "style-loader!css-loader" }]
    }*/
}