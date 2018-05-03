/*      Copyright 2018 Google LLC

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
package com.google.example.resizecodelab.model

import android.os.Handler
import android.os.Looper
import java.util.concurrent.TimeUnit

class DataProvider {

    companion object {
        private const val LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus bibendum orci eu sapien sollicitudin, in vestibulum turpis egestas. Vestibulum cursus nunc in risus sollicitudin, sed ultricies magna finibus. Sed ex arcu, malesuada a magna et, mollis ullamcorper enim. Nulla velit dui, tempus ullamcorper est vel, volutpat convallis nibh. Cras at lacinia risus, nec molestie risus. Fusce a semper magna. Suspendisse nunc nibh, lobortis eget varius et, molestie eget nulla. Phasellus semper ac lectus ac convallis. Vestibulum imperdiet convallis ornare. Aliquam erat volutpat."
        private const val PRODUCT_DESC = "This Whizzbuckling Whipplebot is the finest on the market. Bold, bright design meets crisp, clean functionality in a package that is indescribably satisfying. \n\nWithout compromising the finest quality of craftmanship, this whipplebot provides superior functionality in a package that is unbeatable. Made to last, made to use, made to amaze. This whipplebot will provide decades of delight and become one of the family. Don't settle for mediocre when you can own this amazing Whipplebot."
        private const val REVIEW_TEXT_1 = "This is simply the best Whipplebot I have ever used. In fact, while cleaning the kitchen, I found this to be indispensable. Excellent style, great colours, and it has a certain satisfying feeling that comes along with its use. I would recommend this to anyone, and in fact, I have. Even young people and those slightly older will benefit from its use. Highly recommended."
        private const val REVIEW_TEXT_2 = "A solid whipplebot, excellent value for price."
        private const val REVIEW_TEXT_3 = "Pleasantly surprised. My husband bought this as an anniversary gift in place of the usual tie. At first I had no idea what I would use it for but after getting used to the ergonomic functions, I find I use this whipplebot every day! My only complaint is that I now think we'll have to get one for my mother."
        private const val REVIEW_TEXT_4 = "Works as intended. This is no great work of art, rather its ostentatious lines offend contemporary style in the most particular way. However, as it works precisely as described and does exactly as intended, we are prepared to put forth that it is a most excellent Whipplebot. Yours etc."
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    private val appData = AppData(title ="Whizzbuckling Whipplebot",
            developer = "Ostentatious Objects Inc.",
            description = PRODUCT_DESC,
            shortDescription = "${PRODUCT_DESC.substring(0, 120)}...",
            reviews = arrayOf(Review("Arthur Dent", REVIEW_TEXT_1),
                    Review("Trillian Lancaster", REVIEW_TEXT_2),
                    Review("Goody Twoshoes", REVIEW_TEXT_3),
                    Review("Maxentius", REVIEW_TEXT_4)))

    fun fetchData(listener: Listener) {
        mainHandler.postDelayed({ listener.onSuccess(appData) },
                TimeUnit.SECONDS.toMillis(5))
    }

    interface Listener {
        fun onSuccess(appData: AppData)
    }
}