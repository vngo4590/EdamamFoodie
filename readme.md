# Final Project Readme (This project received HD)

Input API: Edamam Food Database API https://developer.edamam.com/food-database-api

Output API: Pastebin

Claimed Tier: High Distinction

Credit Optional Feature 1: User Configurable Logging

Credit Optional Feature 2: Theme song

Distinction Optional Feature: Spinning Process Indicator (Loading Bar)

High Distinction Optional Feature: Reddit API

## How to start the program

Compile the program:

```bash
gradle clean build
```

If you want to run Pastebin API, Edamam Food Database & Reddit APIs, set 1 argument command as follows:

1. For online run: `gradle run --args="online"`
2. For offline run: `gradle run --args="offline"`

If you want to set both Pastebin and Reddit API's and Edamam Food Database APIs with different mode, you can set them by
this standard:

```bash
gradle run --args="<Edamam API mode> <Both Pastebin & Reddit API mode>"
```

For instance, `gradle run --args="online offline"` this means that Edamam API will be online, and both Pastebin and
Reddit APIs will be offline.

If you want to set each individual API, then you can set them as so:

```bash
gradle run --args="<Edamam API mode> <Pastebin API mode> <Reddit API mode>"
```

For instance, `gradle run --args="online offline online"`
this means that Edamam API will be online, Pastebin will be offline and Reddit API will be online.

## Prerequisite

Before you run the app, make sure you set the environment variables as follows:

1. `PASTEBIN_API_KEY` for the Pastebin API key specified on the Pastebin API
   doc [https://pastebin.com/doc_api](https://pastebin.com/doc_api).
2. `INPUT_API_APP_ID` for the `app_id` variable specified on Edamam API
   doc [https://developer.edamam.com/food-database-api-docs](https://developer.edamam.com/food-database-api-docs)
3. `INPUT_API_KEY` for the `app_key` variable specified on Edamam API
   doc [https://developer.edamam.com/food-database-api-docs](https://developer.edamam.com/food-database-api-docs)

For reddit API, follow this page [https://www.reddit.com/prefs/apps](https://www.reddit.com/prefs/apps)
to get your own app key and client secret
(Note that you should create our app via "script" mode).

Once you have obtained your app key and client secret, you can just add them to your environment variables list:

1. `REDDIT_APP_ID` for the Reddit App Key.
2. `REDDIT_APP_SECRET` for the Reddit Client secret.

## How to access the functionalities

### Entering an allergen

When the application is loaded, you will see a form. This form allows you set your allergen. You must enter an allergen
in order to use the program. Any input that is blank will not be accepted.

1. To enter an allergen, type your allergen in the “Your allergen” box, and then press on “add” button.
2. Once you are happy with your choices, press “Go to add ingredient” to enter an ingredient.

Note: this page can be viewed again if you select the menu option “main > Enter Ingredients.” That is because you need
to reconfirm your allergen if you wish to enter new ingredient.

### Entering an ingredient

1. To enter an ingredient, type your ingredient in the “Your ingredient” box, and then press on “add” button. If you
   don’t want your current selections, simply reset them by pressing on “reset” button.
2. If you entered an ingredient that matches with your allergen, you will be asked to either choose that ingredient (by
   clicking Yes) or not. The ingredient will be added if you clicked yes.
3. Once you are happy with your choices, press “Submit” !

### Viewing & Choosing ingredients

After you have submitted your form, you will see a scroll panel of all possible ingredients that API could provide for
your selections.

To pick an ingredient, simply pick a measure from the “Measure” choice button, enter your quantity, and then press on
“Choose” button. After you have selected your values, you should be able to see the “Chosen Ingredients” panel that
lists your chosen selections. If you want to delete your selections, click on the “Reset” button.

Once you are happy with your choices, press on “Submit” button to view your nutrient information.

If you would like to view more ingredients, press on the “Load Next Page” button to load the next page of your
ingredient list.

Note: After submitting, you can come back to this page and select new ingredients or change your existing selections.

### Selecting nutrient value

While you are selecting a measure for your ingredients, you can select a nutrient.

To enter a nutrient value, you need to first select your nutrient by selecting on the “Pick a Nutrient” box. A list of
nutrients will appear, and you can pick whichever you want. Then, you just need to type your ingredient in the “min” or
“max” box to set the minimum or maximum values as you wish. Note that, if you want to input a range between maximum and
minimum, simply enter your values on both boxes, otherwise either the boxes will suffice. To add your selection, press
on “add” button, and if you don’t want your current selections, simply reset them by pressing on “reset” button.

### Viewing Stacked Bar Chart of the nutritional information against the nutrient maximum values

To view the Stacked Bar Chart, you need to be on the page where you see nutrient information of your choices. On this
page, click on the “View Stacked Bar Chart” button and the chart will show up!

It is worth noting that the stacked bar percentage will use the total nutritional value of the ingredient to compare
against the user set maximum.

### Sending report to Pastebin

To send the report to Pastebin, you need to be on the page where you see nutrient information of your choices. There’s a
“Send Pastebin Report” button for each ingredient or "Send All Reports to Pastebin" if you would like to send all the
ingredients, and you can send the report each of them individually. Once the button is clicked, you should be able to
see a link. You just need to copy this link and paste it on the web browser.

The report is of the format:

```bash
(<food name>, <measure>, <quantity value>) <nutrient name> <total nutrient value>
```

### Login to Reddit

When you first load the app, you should be able to see a `Log in to Reddit` button. To login to Reddit, use your
`username` and `password` of your Reddit Account. Before you log in, make sure that you have set `REDDIT_APP_ID`
and `REDDIT_APP_SECRET` environment variables!

### Sending report to Reddit

To send the report to Reddit, you need to be on the page where you see nutrient information of your choices. There’s a
“Send Reddit Report” button for each ingredient or "Send All Reports to Reddit" if you would like to send all the
ingredients, and you can send the report each of them individually. Once the button is clicked, you should be able to
see a link. You just need to copy this link and paste it on the web browser. The report is of the format:

```bash
(<food name>, <measure>, <quantity value>) <nutrient name> <total nutrient value>
```

### Playing or Pausing Music

Upon loading the app or at any stage of your user interaction, you should be able to see 2 buttons "Play Music" and "
Stop Music". Click on "Play Music" if you want the music to resume or click on "Stop Music" if you want the music to
stop.

## How to access the extra functionalities

### Logging

The log level is set to ALL at the beginning. To change the Log level, simply click on `Logging` on the menu bar and
then select the Log level you want. Whenever you interacted with your UI or if there's any data mutation, the log will
get automatically populated in both the terminal and in the `temp.log` file.

### Loading Spinning Bar

Whenever there's any api all, a loading process bar will appear at the bottom of the screen. It will get turned off when
a particular request has been completed.

## Implemented Features

1. Caching
2. Threading
3. MVP
4. Logging
5. Playing and Pausing Music Theme
6. Spinning Process Indicator
7. Reddit Posting Feature

## References

1. Youtube.com. 2020. YOASOBI - Racing into the Night (Lo-fi Remix). [online] Available
   at: <https://www.youtube.com/watch?v=0dHwQzByszw> [Accessed May 2022].
2. GitHub. OAuth2 · reddit-archive/reddit Wiki. [online] Available
   at: <https://github.com/reddit-archive/reddit/wiki/OAuth2#token-retrieval-code-flow> [Accessed May 2022].
3. Docs.oracle.com. Overview (Java SE 17 & JDK 17). [online] Available
   at: <https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/> [Accessed May 2022].
4. Goers, R. and Gregory, G., Log4j – Log4j 2 API. [online] Logging.apache.org. Available
   at: <https://logging.apache.org/log4j/2.x/manual/api.html> [Accessed 17 May 2022].
5. Square.github.io. n.d. Retrofit. [online] Available at: <https://square.github.io/retrofit/> [Accessed May 2022].