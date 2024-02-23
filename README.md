# DRESTCLI
This application is a CLI REST Client. It relies on `/home/<user>/.drestcli` where the configuration is saved in JSON format.

## How to install
To install the application just clone the repository and run `./build.sh`
it will create the folder structure inside your home folder where you can set the 
configuration. 

It also will modify your `.bashrc` file in order to create an alias for the execution.

For the moment is just available for Linux users.

## How to run
To run the application use `drestcli --help`, it will print an information with available commands.

## Folder structure and configuration

Inside `.drestcli` you will find different folders: `configuration`, `collection` and `authentication`

### Configuration
`configuration` is where you can save you environment variables. Using JSON files
for that. The structure of the JSON is the following:

```json
   [
        {
            "key": "variableName",
            "value": "valueOfVariable"
        }
   ]
```

To use the variables on some other file you can just write `${variableName}$` and it will be
replaced by `valueOfVariable`

In order to use a configuration: `drestcli --env <name>`


### Collection
`collection` folder is where you can save your collections. You can have subfolder
here to have multiple collections. The structure of the collection JSON is:

```json
   { 
        "method": "POST",
        "url": "http://example.com",
        "headers": [
            { "key": "Content-Type", "value": "application/json" }
        ],
        "body": "json body"
   }
```

You can perform a call using `drestcli --env <name> --call <folder/file>`

### Authentication
`authentication` folder is basically done for `oauth2` configuration. It will retrive a new token for you.
The structure of the JSON is:

```json
{
    "grant_type": "client_credentials",
    "token_url": "url",
    "client_id": "client id",
    "client_secret": "client secret"
}
```

You can retrieve a new token using `drestcli --oauth2 <name>`