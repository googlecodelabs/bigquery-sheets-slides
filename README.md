# Code that Final Mile: Big Data Analysis to Slide Presentation

## Pre-requisites

- Access to the internet and a web browser
- A Google account (G Suite accounts may require administrator approval)
- Basic JavaScript skills

## Description

This intermediate Google Apps Script codelab shows you how to build a sample app that calls
the Google BigQuery API to perform a query on one of its public data sets; in this case, a query
that determines the top 10 most common words in Shakespeare's works. BigQuery has a collection
of public data sets, so you're free to issue other queries against this or any other data table
your desire in doing this exercise.

The app stores the query results from BigQuery into a newly-created Google Sheet. The app then
create a chart based on the results data. Finally, it creates a new Google Slides presentation
with one slide featuring the data from the spreadsheet and another with the chart from the Sheet.

The codelab supported by this repository can be found at https://g.co/codelabs/bigquery-sheets-slides). Follow its step-by-step instructions and refer to the corresponding repo folder as necessary to see where your app should be at the end of each step. The `final` subdirectory contains the completed, fully-working sample. The manifest file (see below) stays constant throughout the codelab and thus is stationed at the top-level of this repo. There are only 2 files for this entire sample:
The sample app was inspired by other samples available from the [Google
Developers](http://developers.google.com) team, combining and build on top of both of them:

- `appsscript.json`: Apps Script application manifest
- `final/bq-sheets-slides.js`: application code

## Support

- [BigQuery API support channels](https://cloud.google.com/bigquery/support)
- [Apps Script support channels](https://developers.google.com/apps-script/support)
- [Sheets API support channels](https://developers.google.com/sheets/api/support)
- [Slides API support channels](https://developers.google.com/slides/support)
