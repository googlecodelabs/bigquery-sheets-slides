/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Filename for data results
var QUERY_NAME = "Most common words in all of Shakespeare's works";
// Replace this value with your Google Cloud API project ID
var PROJECT_ID = '';
if (!PROJECT_ID) throw Error('Project ID is required in setup');

/**
 * Runs a BigQuery query; puts results into Sheet. You must enable
 * the BigQuery advanced service before you can run this code.
 * @see http://developers.google.com/apps-script/advanced/bigquery#run_query
 * @see http://github.com/gsuitedevs/apps-script-samples/blob/master/advanced/bigquery.gs
 *
 * @returns {Spreadsheet} Returns a spreadsheet with BigQuery results
 * @see http://developers.google.com/apps-script/reference/spreadsheet/spreadsheet
 */
function runQuery() {
  // Replace sample with your own BigQuery query.
  var request = {
    query:
        'SELECT ' +
            'LOWER(word) AS word, ' +
            'SUM(word_count) AS count ' +
        'FROM [bigquery-public-data:samples.shakespeare] ' +
        'GROUP BY word ' +
        'ORDER BY count ' +
        'DESC LIMIT 10'
  };
  var queryResults = BigQuery.Jobs.query(request, PROJECT_ID);
  var jobId = queryResults.jobReference.jobId;

  // Wait for BQ job completion (with exponential backoff).
  var sleepTimeMs = 500;
  while (!queryResults.jobComplete) {
    Utilities.sleep(sleepTimeMs);
    sleepTimeMs *= 2;
    queryResults = BigQuery.Jobs.getQueryResults(PROJECT_ID, jobId);
  }

  // Get all results from BigQuery.
  var rows = queryResults.rows;
  while (queryResults.pageToken) {
    queryResults = BigQuery.Jobs.getQueryResults(PROJECT_ID, jobId, {
      pageToken: queryResults.pageToken
    });
    rows = rows.concat(queryResults.rows);
  }

  // Return null if no data returned.
  if (!rows) {
    return Logger.log('No rows returned.');
  }

  // Create the new results spreadsheet.
  var spreadsheet = SpreadsheetApp.create(QUERY_NAME);
  var sheet = spreadsheet.getActiveSheet();

  // Add headers to Sheet.
  var headers = queryResults.schema.fields.map(function(field) {
    return field.name.toUpperCase();
  });
  sheet.appendRow(headers);

  // Append the results.
  var data = new Array(rows.length);
  for (var i = 0; i < rows.length; i++) {
    var cols = rows[i].f;
    data[i] = new Array(cols.length);
    for (var j = 0; j < cols.length; j++) {
      data[i][j] = cols[j].v;
    }
  }

  // Start storing data in row 2, col 1
  var START_ROW = 2;      // skip header row
  var START_COL = 1;
  sheet.getRange(START_ROW, START_COL, rows.length, headers.length).setValues(data);

  // Return the spreadsheet object for later use.
  return spreadsheet;
}

/**
 * Uses spreadsheet data to create columnar chart.
 * @param {Spreadsheet} Spreadsheet containing results data
 * @returns {EmbeddedChart} visualizing the results
 * @see http://developers.google.com/apps-script/reference/spreadsheet/embedded-chart
 */
function createColumnChart(spreadsheet) {
  // Retrieve the populated (first and only) Sheet.
  var sheet = spreadsheet.getSheets()[0];
  // Data range in Sheet is from cell A2 to B11
  var START_CELL = 'A2';  // skip header row
  var END_CELL = 'B11';
  // Place chart on Sheet starting on cell E5.
  var START_ROW = 5;      // row 5
  var START_COL = 5;      // col E
  var OFFSET = 0;

  // Create & place chart on the Sheet using above params.
  var chart = sheet.newChart()
     .setChartType(Charts.ChartType.COLUMN)
     .addRange(sheet.getRange(START_CELL + ':' + END_CELL))
     .setPosition(START_ROW, START_COL, OFFSET, OFFSET)
     .build();
  sheet.insertChart(chart);
}

/**
 * Runs a BigQuery query, adds data and a chart in a Sheet.
 */
function createBigQueryPresentation() {
  var spreadsheet = runQuery();
  Logger.log('Results spreadsheet created: %s', spreadsheet.getUrl());
  createColumnChart(spreadsheet);
}
