# Dynamic documents for developers - basic training

Welcome to the sample project for **Dynamic documents for developers - basic training**.
This repository contains the project build during the basic training, serving as a reference guide for building applications with [`DynamicFormsModule`](https://across-docs.foreach.be/across-site/production/dynamic-forms-module/0.0.1/index.html).
The base of the application was generated using [Across Initializr](http://start-across.foreach.be/).

In this project, a web application has been developed for a company that offers trainings to a broad public. 
Visitors of the site can register for a training, which can then be further processed through the back office.

## Content

Within this project, you'll find sample code for the following subjects:

* Sample [definitions](https://across-docs.foreach.be/across-site/production/dynamic-forms-module/0.0.1/definitions/creating-a-document-definition.html) for a `Training` and `Registration` document
  * Includes use of various [field types](https://across-docs.foreach.be/across-site/production/dynamic-forms-module/0.0.1/field-types/index.html)
  * Including corresponding [`view`](https://across-docs.foreach.be/across-site/production/dynamic-forms-module/0.0.1/definitions/creating-a-view-definition.html) definitions to customize the list view
* [Controller for a frontend](https://across-docs.foreach.be/across-site/production/dynamic-forms-module/0.0.1/guides/general/creating-a-simple-frontend-form.html) registration page
  * Renders a form based on the `Registration` definition
  * Supports the creation of `Registration` documents when the form is submitted
  * Validation of the submitted form 

## Extras

Aside of the content that was presented during the training itself, it also contains the following extras:

* A `DefinitionInstaller` to create a sample data set and [import the aforementioned definitions](https://across-docs.foreach.be/across-site/production/dynamic-forms-module/0.0.1/import-export/importing-definitions.html)
* A [custom field validator](https://across-docs.foreach.be/across-site/production/dynamic-forms-module/0.0.1/validators/creating-a-field-validator.html) that supports the validation of email addresses
* A `DocumentsInstaller` to [import sample documents](https://across-docs.foreach.be/across-site/production/dynamic-forms-module/0.0.1/import-export/importing-documents.html) for the `Training` definition  

# General project information

## Starting the website on your local machine
Just start `DemoApplication` with the *dev* profile and keep it running.
This will startup the website using a local database on port **8080**.

Browsing to http://localhost:8080 should give you the homepage.

> NOTE: you can force the dev profile by specifying system property *-Dspring.profiles.active=dev* 

## Administration UI
The administration UI is available on http://localhost:8080/admin.
Default username and password is admin/admin.

## Front-end development
When starting on your local machine, you should get instant reloading of all static files.
Simply make a change and refresh the page in your browser.

All front-end related static files are located in `src/main/resources/views`.

> WARNING: _All files in `src/main/resources` will be packaged and deployed with the website.
> Do not put any files anywhere in that folder if they are not supposed to be deployed!

### LiveReload integration
The application supports LiveReload.
If you have the plugin in your browser, just activating it on the site should be enough.

### Templating system
The application is built using Thymeleaf 3 for templating.
User documentation: http://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html

## Troubleshooting
### Resetting the local database
Starting up locally will create a local database in a folder `local-data/db`.
If you find any problems starting up the application, try removing this folder and then restarting.

