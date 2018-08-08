function saveModule(){
    console.log("saving module")
}

function runModule(){
    console.log("running module")
}

var modulesList = [];
window.onload =
    fetch('http://localhost:8000/', {
        method: 'post',
        headers: {
            'operation': "loadAllModule"
        },
    }).then(response => response.text())
        .then((body) => {
            modulesList = body.trim().split(" ");
        }).then(a => {
            var select = document.getElementById("ModuleSelector")
            for (var i = 0; i < modulesList.length; i++) {
                var option = document.createElement("option");
                option.value = modulesList[i];
                option.text = modulesList[i];
                select.appendChild(option);
            }
    });

function loadModule(moduleName) {
    var ModuleLines = [];
    fetch('http://localhost:8000/', {
        method: 'post',
        headers: {
            'operation': "loadModule"
        },
        body: moduleName
    }).then(response => response.text())
        .then((body) => {
            moduleCodeMirror.setValue("");
            moduleCodeMirror.replaceRange(body, CodeMirror.Pos(0));
    });
}