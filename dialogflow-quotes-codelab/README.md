Get Started with Dialogflow: STEM Quotes
============

This repository contains a sample solution to the "Get Started with Dialogflow:
STEM Quotes" codelab, along with the data and code for the backend used in the
codelab to serve quotes. 

Sample solution
------------

A solution to the codelab can be accessed by downloading the
'MyQuoteMaster.zip' file and importing it into Dialogflow, as described in the
codelab.

Backend data
--------------

The backend serves quotes and biographies of famous women in STEM.

The full set of data used can be found in the 'quotes.csv' file.

Backend code
--------------

The service is written using [Flask-RESTful](https://flask-restful.readthedocs.io)
and deployed using [App Engine](http://cloud.google.com/appengine/docs/) on
[Google Cloud Platform](https://cloud.google.com/).

Before running or deploying, install the dependencies using
[pip](http://pip.readthedocs.io):

    pip install -t lib -r requirements.txt

To run the service locally, you can run

    dev_appserver.py .

Then you can test an example request:

    curl -X POST http://localhost:8080/quotesearch -H "Content-Type: application/json" -d '{"result": {"action": "get_quote_event", "parameters": {"author": "Grace Hopper", "topic": "technology"}}}' 

Example response:

    {"followupEvent": {"data": {"author": "Grace Hopper", "quote": "The application of systems techniques has been successful in scientific and technical applications . . . It meets difficulty when it is applied in social and political situations largely because people are not 'well-behaved' mathematical functions, but can only be represented by statistical approximations, and all of the extremes can and do occur."}, "name": "respond_with_quote"}}

To deploy, run

    gcloud app deploy app.yaml

The service has one endpoint '/quotesearch'. It accepts POST requests with
Content-type application/json as sent by Dialogflow. See the full request format
[here](https://dialogflow.com/docs/fulfillment#request). It handles 4 actions:

<table>
  <tbody>
    <tr>
      <th width="20%">Action</td>
      <th width="40%">Body</td>
      <th width="40%">Output</td>
    </tr>
    <tr>
      <td>
        <div>get_quote_event<br><br>Get a quote response as a followup event.</div>
      </td>
      <td>
        <div>{<br>&emsp;…<br>&emsp;&quot;result&quot;: {<br>&emsp;&emsp;&quot;parameters&quot;: {<br>&emsp;&emsp;&emsp;&quot;author&quot;: &quot;Grace Hopper&quot;,<br>&emsp;&emsp;&emsp;&quot;topic&quot;: &quot;computers&quot;<br>&emsp;&emsp;}, <br>&emsp;&emsp;&quot;action&quot;: &quot;get_quote_event&quot;<br>&emsp;&emsp;…<br>&emsp;}<br>}<br><br>Both parameters are optional.</div>
      </td>
      <td>
        <div>{<br>&emsp;&quot;followupEvent&quot;: {<br>&emsp;&emsp;&quot;name&quot;: &quot;respond_with_quote&quot;,&emsp;&emsp;<br>&emsp;&emsp;&quot;data&quot;: {<br>&emsp;&emsp;&emsp;&quot;quote&quot;: &quot;computers&quot;,   <br>&emsp;&emsp;&emsp;"author": "Grace Hopper"<br>&emsp;&emsp;}<br>&emsp;}<br>}<br><br>|data| will be empty if there is no applicable quote.</div>
      </td>
    </tr>
    <tr>
      <td>
        <div>get_quote_response<br><br>Get a quote response as response text.</div>
      </td>
      <td>
        Same as above, except<br><br>&quot;action&quot;: &quot;get_quote_response&quot;
      </td>
      <td>
        <div>{<br>&emsp;"displayText": "Here’s a quote...",<br>&emsp;"speech": "Here’s a quote..."<br>}</div>
      </td>
    </tr>
    <tr>
      <td>
        <div>get_bio_event<br><br>Get an author bio response as a followup event.</div>
      </td>
      <td>
        <div>{<br>&emsp;…<br>&emsp;&quot;result&quot;: {<br>&emsp;&emsp;&quot;parameters&quot;: {<br>&emsp;&emsp;&emsp;&quot;author&quot;: &quot;Grace Hopper&quot;<br>&emsp;&emsp;}, <br>&emsp;&emsp;&quot;action&quot;: &quot;get_bio_event&quot;<br>&emsp;&emsp;…<br>&emsp;}<br>}<br><br>The |author| parameter is required.</div>
      </td>
      <td>
        <div>{<br>  &quot;followupEvent&quot;: {<br>&emsp;&emsp;&quot;name&quot;: &quot;respond_with_bio&quot;,&emsp;&emsp;<br>&emsp;&emsp;&quot;data&quot;: {<br>&emsp;&emsp;&emsp;&quot;bio&quot;: &quot;Grace Hopper...&quot;<br>&emsp;&emsp;}<br>&emsp;}<br>}<br><br>|data| will be empty if there is no matching bio.</div>
      </td>
    </tr>
    <tr>
      <td>
        <div>get_bio_response<br><br>Get an author bio response as response text.</div>
      </td>
      <td>
        Same as above, except<br><br>&quot;action&quot;: &quot;get_bio_response&quot;
      </td>
      <td>
        <div>{<br>&emsp;"displayText": "Here’s the bio...",<br>&emsp;"speech": "Here’s the bio..."<br>}</div>
      </td>
    </tr>
  </tbody>
</table>


License
-------

Copyright 2018 Google, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
