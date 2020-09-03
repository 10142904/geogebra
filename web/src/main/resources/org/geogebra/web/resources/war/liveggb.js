(function() {
    function LiveApp(parentClientId, embedLabel, users) {
        this.api = null;
        this.eventCallback;
        this.users = users || {};
        this.clientId = parentClientId;
        this.embeds = {};
        this.sendEvent = function() {
            var type = arguments[0];
            var content = arguments[1];
            var event = {
                "type": type,
                "content": content,
                "embedLabel": embedLabel,
                "clientId": this.clientId
            }
            if (arguments[2]) {
                event.label = arguments[2];
            }
            this.eventCallback(event);
        };
        this.evalCommand = function(command) {
            this.unregisterListeners();
            this.api.evalCommand(command);
            this.registerListeners();
        };

        this.evalXML = function(xml) {
            this.unregisterListeners();
            this.api.evalXML(xml);
            this.api.evalCommand("UpdateConstruction()");
            this.registerListeners();
            var that = this;
            setTimeout(function() {
                that.api.getAllObjectNames("embed").forEach(that.initEmbed.bind(that));
            }, 500);//TODO no timeout
        };

        this.setXML = function(xml) {
            this.unregisterListeners();
            this.api.setXML(xml);
            this.api.evalCommand("UpdateConstruction()");
            this.registerListeners();
        };

        this.initEmbed = function(label) {
            if (this.embeds[label]) {
                return;
            }
            var calc = (this.api.getEmbeddedCalculators() || {})[label];
            if (calc) {
                if (calc.registerClientListener) {
                    var calcLive = new LiveApp(this.clientId, label, this.users);
                    calcLive.api = calc;
                    calcLive.eventCallback = this.eventCallback;
                    calcLive.registerListeners();
                    this.embeds[label] = calcLive;
                }
            }
        }

        let objectsInWaiting = [];
        let updateCallback;

        let dispatchUpdates = (function() {
            if (!updateCallback) {
                const that = this;
                updateCallback = setTimeout(function() {
                    const tempObjects = objectsInWaiting.slice();
                    objectsInWaiting = [];

                    for (let i = 0; i < tempObjects.length; i++) {
                        const label = tempObjects[i];
                        const embed = that.api.getEmbeddedCalculators()[label];

                        if (embed && embed.controller) {
                            that.sendEvent("evalGMContent", embed.toJSON(), label);
                        }

                        let commandString = that.api.getCommandString(label, false);
                        if (commandString) {
                            that.sendEvent("evalCommand", label + " = " + commandString, label);
                        } else {
                            let xml = that.api.getXML(label);
                            that.sendEvent("evalXML", xml, label);
                        }
                        that.sendEvent("previewRefresh");
                    }

                    updateCallback = null;
                }, 200);
            }
        }).bind(this);

        // *** UPDATE LISTENERS ***
        let updateListener = (function(label) {
            console.log("update event for " + label);
            if (!objectsInWaiting.includes(label)) {
                objectsInWaiting.push(label);
                dispatchUpdates();
            }
        }).bind(this);

        // *** ADD LISTENERS ***
        var addListener = (function(label) {
            console.log(label + " is added");
            var image = this.api.getImageFileName(label);
            var that = this;
            if (image) {
                var json = this.api.getFileJSON();
                json.archive.forEach(function(item) {
                    if (item.fileName == image) {
                        that.sendEvent("addImage", JSON.stringify(item));
                    }
                })
            }
            var xml = this.api.getXML(label);

            var definition = this.api.getCommandString(label);
            if (definition) {
                this.sendEvent("evalXML", this.api.getAlgorithmXML(label), label);
            } else {
                this.sendEvent("evalXML", xml, label);
            }
            this.sendEvent("previewRefresh");
            window.setTimeout(function(){
                that.initEmbed(label);
            },500); //TODO avoid timeout

        }).bind(this);

        // *** REMOVE LISTENERS ***
        var removeListener = (function(label) {
            console.log(label + " is removed");
            this.sendEvent("deleteObject", label);
        }).bind(this);

        // *** CLIENT LISTENERS ***
        var clientListener = (function(event){
            switch (event[0]) {
                case "updateStyle":
                    var label = event[1];
                    console.log(label + " has changed style");

                    var xml = this.api.getXML(label);
                    this.sendEvent("evalXML", xml);
                    break;

                case "setMode":
                    console.log("setMode(" + event[2] + ")");
                    break;

                case "editorKeyTyped":
                    var state = this.api.getEditorState();
                    console.log(state);
                    this.sendEvent("setEditorState", state, event[1]);
                    break;

                case "editorStop":
                    this.sendEvent("setEditorState", "{content:\"\"}");
                    break;

                case "deselect":
                    this.sendEvent("evalCommand", "SelectObjects[]");
                    break;

                case "select":
                    this.sendEvent("evalCommand", "SelectObjects[" + event[1] + "]");
                    break;

                case "undo":
                case "redo":
                case "addPolygonComplete":
                    console.log(event[0], "sending whole xml");

                    var xml = this.api.getXML();
                    //console.log(xml);
                    this.sendEvent("setXML", xml);
                    break;

                case "addSlide":
                    this.sendEvent(event[0]);
                    break;

                case "removeSlide":
                case "moveSlide":
                case "selectSlide":
                case "clearSlide":
                    this.sendEvent(event[0], event[2]);
                    break;

                case "pasteSlide":
                    this.sendEvent(event[0], event.cardIdx, event.ggbFile);
                    break;

                default:
                    // console.log("unhandled event ", event[0], event);
            }

        }).bind(this);

        this.registerListeners = function() {
            this.api.registerUpdateListener(updateListener);
            this.api.registerRemoveListener(removeListener);
            this.api.registerAddListener(addListener);
            this.api.registerClientListener(clientListener);
        };

        this.unregisterListeners = function() {
            this.api.unregisterUpdateListener(updateListener);
            this.api.unregisterRemoveListener(removeListener);
            this.api.unregisterAddListener(addListener);
            this.api.unregisterClientListener(clientListener);
        };

        this.showHint = function(event) {
            var user = this.users[event.clientId];
            if (user && event.label) {
                this.api.showTooltip(user.name, event.label, user.color);
            }
        }

        this.dispatch = function(last) {
            if (last && last.clientId != this.clientId) {
                target = last.embedLabel ? this.embeds[last.embedLabel] : this;
                if (last.type == "evalXML") {
                    target.evalXML(last.content);
                } else if (last.type == "setXML") {
                    target.setXML(last.content);
                } else if (last.type == "evalCommand") {
                    target.evalCommand(last.content);
                } else if (last.type == "deleteObject") {
                    target.api.deleteObject(last.content);
                } else if (last.type == "setEditorState") {
                    target.unregisterListeners();
                    target.api.setEditorState(last.content, last.label);
                    target.registerListeners();
                } else if (last.type == "addImage") {
                    var file = JSON.parse(last.content);
                    target.api.addImage(file.fileName, file.fileContent);
                } else if (last.type == "addSlide"
                    || last.type == "removeSlide"
                    || last.type == "moveSlide"
                    || last.type == "clearSlide") {
                    target.api.handleSlideAction(last.type, last.content);
                } else if (last.type == "selectSlide") {
                    target.unregisterListeners();
                    target.api.selectSlide(last.content);
                    target.registerListeners();
                } else if (last.type == "previewRefresh") {
                    target.api.previewRefresh();
                } else if (last.type == "pasteSlide") {
                    target.api.handleSlideAction(last.type, last.content, last.label);
                } else if (last.type == "evalGMContent") {
                    var gmApi = target.api.getEmbeddedCalculators()[last.label];
                    if (gmApi) {
                        gmApi.loadFromJSON(last.content);
                    }
                }
                if (last.type != "pasteSlide") { // for slides the label slide label => no hint
                    target.showHint(last);
                }
            }
        };
   }

    window.GeoGebraLive = function(api, id) {
        var mainSession = new LiveApp(id);
        mainSession.api = api;
        this.dispatch = function(last) {
            mainSession.dispatch(last);
        },
        this.addUser = function(user) {
            mainSession.users[user.id] = user;
        }
        this.start = function(events, callback) {
           mainSession.events = events;
           mainSession.eventCallback = callback;
           mainSession.registerListeners();
       }
    }
})();