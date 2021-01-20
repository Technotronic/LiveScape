# LiveScape Audio Tool

LiveScape is an open-source application that simplifies the creation of spatial audio for electronic artworks and interactive installations. The application engine is built on the foundation of <a href="https://www.lwjgl.org/" target="_blank">LWJGL 3.2.3</a> and <a href="https://www.openal.org/" target="_blank">OpenAL 1.1</a> to create a lightweight visual interface for mapping sound object in a virtual space.

**Readme Index**

* [List of Examples](#examples)

---

**Origin**

The idea behind LiveScape originates from the observation of a lack of accessible tools for working with spatial audio. Even though the technologies and libraries behind this principle (like OpenAL) are not new, there is a general lack of a clear approach on how to get started with space and sound. This gap leaves space to come up with a new type of framework to enable others to start working with spatial audio.

Therefore, the goal of LiveScape is to make working with spatial audio as easy as possible so that creators can focus on what they are good at; creating.

---

**Requirements**

In the current state of the application, it is **only possible to run LiveScape on MacOS**. One of the near-future goals of the project is to port the application to Linux/Raspberry Pi OS to create a lower threshold for people who are interested in working with LiveScape.

Since OpenAL en Apple's CoreAudio are quite the efficient libraries, the requirements for running LiveScape are very low. The application runs on **JavaSE-14** (will most probably change in support for Raspberry Pi OS) and uses LWJGL 3.2.3 (with OpenAL 1.1 included).
All libraries needed for development are included in the source code in the `/lib` folder.

# Application Components

**Contexts, sources and listeners**

The main components of what make LiveScape tick are directly inherited from the way OpenAL operates. All created sounds and listeners (virtual inputs and outputs) are placed in a **AudioContext** (<a href="https://github.com/Technotronic/LiveScape/blob/master/src/livescape/audio/" target="_blank">found here</a>). Objects that generate sound are called an **AudioSource**, and the object registering sound are called **AudioListener**.

A context is always connected to a *device*. For MacOS this is always the default audio output device that is selected in your System Preferences (default CoreAudio device). This is a current limitation for the LiveScape engine and another reason to dive into Linux (and subsequently <a href="https://www.alsa-project.org/wiki/Main_Page" target="_blank">ALSA</a>) very soon.

When a context is created, the listener (and its position, orientation and velocity) is assigned. Now that the Context is ready, audio sources can be inserted as well (with the same type of positional parameters).

# Examples

All mentioned examples are prone to changing in this early stage of development. I will try to update the `/examples/basics` folder frequently and add new ones to keep new features included. For human-readability purposes, JSON is chosen for the save file structure. In this section you can find some small examples to tinker with.

**Save Files**

Currently, the best way to create new timelines is to copy one of the existing examples. In the directory of `/examples/basics` there is a `timeline.json` file present that can be edited. *On loading the application this file will automatically be loaded into the LiveScape environment.*

**Type Blocks**

A basic save file structure will look generally like this:
```json
"id": "basics",
"name": "example-one",
"length": 30000,
"listeners": [{
  "id": 0,
  "name": "listener",
  "position": [0.00, 0.00, 0.00]
}],
"sources": [{
}]
```

Every save fill starts with a set of header, assigning a Timeline `id`, `name`, `length`, `listeners` and `sources`. These parameters are mandatory and indicate where sources and listeners are allocated to and how long a timeline can run.

```json
"id": 0,
"name": "source-name",
"file": "source-file-in-audio-folder.wav",
"timeline": {
  "track": 0,
  "start": 0
},
"context": {
  "position": [0.00, 0.00, 0.00],
  "orientation": [0.00, 0.00, 0.00],
  "velocity": [0.00, 0.00, 0.00]
},
"enabled": true,
"gain": 1.0,
"color": 0,
"timestamps": []
```

Inside of the sources array is where individual *AudioSource* references are located. Do note that all audio files of a `/projects/*` folder need to be placed inside the `/audio` folder in order to import them. These audio files also need to be in a **mono .wav-format** for them to be rendered in a spatial matter (this requirement will soon change when a stereo-to-mono converter is built).
