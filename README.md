# Code that Final Mile: Big Data Analysis to Slide Presentation

This intermediate Google Apps Script codelab shows you how to build a sample app that demonstrates
use of the Google BigQuery API to query a public data set and create a Google Slides presentation
with the results data. A Google Sheet is used as an intermediary to store the analysis data as
well as its ability to create charts on its data.

## Pre-requisites

- Access to the internet and a web browser
- A Google account (G Suite accounts may require administrator approval)
- Basic JavaScript skills

## Description

BigQuery first performs a query on one of its public data sets; in this case, a query that
determines the top 10 most common words in Shakespeare's works. BigQuery has a collection of
public data sets, so you're free to issue other queries against this or any other data table
you desire in doing this exercise. The app stores the query results from BigQuery into a
newly-created Google Sheet. The Sheets API is then used to create a chart based on the results
data. Finally, a new Google Slides presentation is created. A new slide is added whereby the
the Sheet's cell data is imported. A second new slide is added embedding (and linking to) the
chart from the Sheet.

This repo supports the developer codelab found at https://g.co/codelabs/bigquery-sheets-slides).
Follow its step-by-step instructions and refer to the corresponding repo folder as necessary to
see where your app should be at the end of each (major) step. The `final` subdirectory contains
the completed, fully-working sample. When you run it, you should get a slide presentation that looks like this:

![generated slide presentation](https://codelabs.developers.google.com/codelabs/bigquery-sheets-slides/img/bd9cafb69564190e.png "generated slide presentation")

The manifest file (see below) stays constant throughout the codelab and thus
is stationed at the top-level of this repo. There are 2 key files for this
entire sample (while the rest are intermediary):

- [`appsscript.json`](https://github.com/googlecodelabs/bigquery-sheets-slides/blob/master/appsscript.json): Apps Script application manifest
- [`final/bq-sheets-slides.js`](https://github.com/googlecodelabs/bigquery-sheets-slides/blob/master/final/bq-sheets-slides.js): application code

This application was also featured in a Google Cloud NEXT 2018 breakout session (although
the demo was borked due to a permission problem): [G Suite + GCP: Building Serverless Applications
with All of Google Cloud](https://cloud.withgoogle.com/next18/sf/sessions/session/156878). Here is
[the video](http://youtu.be/mR1MLi-_biU) for that talk.

## Support

- [BigQuery API support channels](https://cloud.google.com/bigquery/support)
- [Apps Script support channels](https://developers.google.com/apps-script/support)
- [Sheets API support channels](https://developers.google.com/sheets/api/support)
- [Slides API support channels](https://developers.google.com/slides/support)
