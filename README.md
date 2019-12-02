# SWMediaTracker (published as HoloCanon)
An Android app for tracking consumption of canonical Star Wars media. I started writing this app for myself to help me pick out books when I went to the library. Eventually, I realized there might be others out there who wish to use it, so I cleaned it up for production and it is currently published on the Google Play store at https://play.google.com/store/apps/details?id=com.minirogue.starwarscanontracker.

## Installation
The easiest way to install the latest version is through the Google Play store at the link above. Otherwise, you will need to compile the APK from this code and sideload it yourself (surely you can find a guide online for how to do this). Note that this code is missing app/build.gradle (for reasons), so you can't quite compile it as is and will need to add the build.gradle yourself. I plan to include it in a later commit, but it isn't a high priority.

## Usage
The app has three main screens.
* The entry screen ("About This App" in the drawer menu) displays a welcome message, recent updates, and disclaimers.
* The Canon List is the main part of the app and lists all current canonical Star Wars material (assuming the database is up to date). The list supports multiple filtering and sorting options.
* Settings is where you can permanently filter out certain media types that you do not wish to appear in the main list. You can also change the checkbox text to provide a more personalized way to mark content. Finally, you can force an update of your local media database here.

## TODO
Here are some things that I plan to add to the app:
* ~~Saving current filters and sorting so they persist when the user returns to the Canon List screen.~~
* Text search for the list. This should include searching all relevant text fields of the items.
* ~~Clean up the filter selection menu into filter groups with options to filter ONLY selected items or to filter OUT the selected items in a group.~~
* Filter selection could probably still be cleaned up further.
* ~~Add some functionality for series, including checking off an entire series.~~
* Add filtering by publisher (seems like people aren't as into the IDW comics, I guess).
* Add comments to code in case someone wants to contribute or if a potential employer wants to look at the code (Hi, still on the job market as of December 1, 2019).
* ~~Clean up fragment management (OOM error may be possible with current configuration)~~
* Add syncing of user selections across devices. Temporary(?) solution could be to add import/export option.
* Add donation button, in case the users ever decide to feel generous. No plans to ever implement paid features.
* Clean up the backend to use RESTful API requests and JSONs.
* Add Hardcover, Omnibus, and reference books to database

## License
I'm still figuring out the correct license to use here and how to properly include it.
