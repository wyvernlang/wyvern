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


function reload() {
    var newModulesList
    fetch('http://localhost:8000/', {
        method: 'post',
        headers: {
            'operation': "loadAllModule"
        },
    }).then(response => response.text())
        .then((body) => {
            newModulesList = body.trim().split(" ");
        }).then(a => {
        var select = document.getElementById("ModuleSelector")
        for (var i = 0; i < newModulesList.length; i++) {
            if (!modulesList.includes(newModulesList[i])) {
            var option = document.createElement("option");
            option.value = newModulesList[i];
            option.text = newModulesList[i];
            select.appendChild(option);
            modulesList.push(newModulesList[i])
            }
        }
    });
}


function saveModule(){
    var moduleNameToSave = document.getElementById('moduleName').value;
    var code = moduleCodeMirror.getValue();
    modulesList.forEach(function(element) {
        if(element == moduleNameToSave){
            alert("that module name already exists");
            return;
        }
    });
    var moduleToSave = moduleNameToSave + "=:=" + code;
    fetch('http://localhost:8000/', {
        method: 'post',
        headers: {
            'operation': "saveModule"
        },
        body: moduleToSave
    });
}

function runModule(){
    var code = moduleCodeMirror.getValue();
    fetch('http://localhost:8000/', {
        method: 'post',
        headers: {
            'id': id,
            'operation': "interpretModule"
        },
        body: code
    }).then(response => response.text())
        .then((body) => {
            result = body;
            console.log(body)
        }).then(a => {
        if (result.endsWith("\n")) {
            consoleCodeMirror.replaceRange("----------------Output----------------\n", CodeMirror.Pos(consoleCodeMirror.lastLine()));
            consoleCodeMirror.replaceRange(result.substring(0, result.length - 1), CodeMirror.Pos(consoleCodeMirror.lastLine()));
            consoleCodeMirror.replaceSelection("\n", "end");
            consoleCodeMirror.replaceRange("--------------------------------------\n", CodeMirror.Pos(consoleCodeMirror.lastLine()));
        } else if (result != " ") {
            consoleCodeMirror.replaceRange("----------------Output----------------\n", CodeMirror.Pos(consoleCodeMirror.lastLine()));
            consoleCodeMirror.replaceRange(result, CodeMirror.Pos(consoleCodeMirror.lastLine()));
            consoleCodeMirror.replaceSelection("\n", "end");
            consoleCodeMirror.replaceRange("--------------------------------------\n", CodeMirror.Pos(consoleCodeMirror.lastLine()));

        }
        lastUpdated = consoleCodeMirror.lastLine();
    }).then(a => {
        readOnlyLines.push(consoleCodeMirror.lastLine() - 1);
    });
}

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