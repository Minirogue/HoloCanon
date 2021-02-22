# SWMediaTracker (published as HoloCanon)
An Android app for tracking consumption of canonical Star Wars media. I started writing this app for myself to help me pick out books when I went to the library. Eventually, I realized there might be others out there who wish to use it, so I cleaned it up for production and it is currently published on the Google Play store at https://play.google.com/store/apps/details?id=com.minirogue.starwarscanontracker.


## Installation
The easiest way to install the latest version is through the Google Play store at the link above. Otherwise, you will need to compile the APK from this code and sideload it yourself (surely you can find a guide online for how to do this).


## Usage
The app has four main screens.
* The entry screen ("About This App" in the drawer menu) displays a welcome message, recent updates, and disclaimers.
* The Canon List is the main part of the app and lists all current canonical Star Wars material (assuming the database is up to date). The list supports multiple filtering and sorting options.
* Filter selection, to make looking through the list easier.
* Settings is where you can permanently filter out certain media types that you do not wish to appear in the main list. You can also change the checkbox text to provide a more personalized way to mark content. Finally, you can force an update of your local media database here.

## TODO
Here are some things that I plan to add to the app:
* Text search for the list. This should include searching all relevant text fields of the items.
* Add comments to code.
* Add syncing of user selections across devices. Temporary(?) solution could be to add import/export option.
* Add option to export a list of checked/unchecked items to a txt or pdf.
* Add donation button, in case the users ever decide to feel generous. No plans to ever implement paid features.
* Clean up the backend to use http requests and JSONs.
* Add Hardcover, Omnibus, and reference books to database.
* Expand support for different screen sizes (particularly tablets).

## License
I'm still figuring out the correct license to use here and how to properly include it.
