# This is an extensible text-editor application.

## Running the application

To start the application, run the following command:

```
wyvern path/to/main.wyv
```

## Adding a plugin

The plugin must be of type `Plugin`.

To add a plugin:
1. Add the plugin file (`.wyv` file) to the `plugins` folder.
2. Make changes to the `textEditor.wyv` file in the areas marked with asterisks and labeled with numbered steps:
  * Step 1: Import the plugin.
  * Step 2: Instantiate the plugin passing in appropriate resources.
  * Step 3: Register plugin with menu or run it on setup.
    - Register a plugin with the menu so that the user can activate it on demand during the text-editor execution. (Registered plugins are usually those that depend on the user's input, e.g., a plugin that counts the number of words in the opened file.)
    - Make the plugin run once on setup. (Plugins that are run on setup are usually those that set a configuration for the text editor, e.g., a plugin that sets the text editor's theme.)
