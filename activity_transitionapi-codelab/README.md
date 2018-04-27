Detecting User Activity changes using Activity Recognition Transition API
============

Phones are our most personal devices we bring with us everywhere, but until now it's been hard for apps to adjust their experience to a user's continually changing environment and activity. We've heard from developer after developer that they're spending valuable engineering time to combine various signals like location and sensor data just to determine when the user has started or ended an activity like walking or driving. Even worse, when apps are independently and continuously checking for changes in user activity, battery life suffers. Activity Recognition Transition API helps in solving these problems by providing a simple API that does all the processing for you and just tells you what you actually care about: when a user's activity has changed.

As an example, a messaging app can  ask - tell me when the user has entered or exited the vehicle to set the user's status to busy. Similarly, a parking detection app can ask - tell me when the user has exited a vehicle and started walking.

Pre-requisites
--------------
Android API Level >v14
Android Build Tools >v21
Google Support Repository

<!--These should be learning materials, not software requirements; samples
    should be entirely self-contained. Format as URLs in a list.-->
- [Example](http://github.com/googlesamples/example)

Getting Started
---------------
The sample uses the Gradle build system.

Support
-------

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub.

License
-------

Copyright 2016 Google, Inc.

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
