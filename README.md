# SWMediaTracker (published as HoloCanon)
An Android app for tracking consumption of canonical Star Wars media. I started writing this app for myself to help me pick out books when I went to the library. Eventually, I realized there might be others out there who wish to use it, so I cleaned it up for production and it is currently published on the Google Play store at https://play.google.com/store/apps/details?id=com.minirogue.starwarscanontracker.


## Installation
The easiest way to install the latest version is through the Google Play store at the link above. Otherwise, you will need to compile the APK from this code and sideload it yourself (surely you can find a guide online for how to do this).

## Updating the Database
The database itself is stored in `api/src/main/resources/media.csv`. Here are some guidelines for updating it:
* The `ID`, `title`, `type`, `released`, and `publisher` columns are all mandatory
  * `ID` should increase sequentially by 1 for each new entry
  * `title` should match the title of the book, movie, etc. For comic books, this should include the issue number with enough leading 0's to ensure proper sequential sorting.
  * `type` should match one of the existing types exactly, or it may not parse correctly in the code. To add a new type, `MediaType` in `Company.kt` needs to be updated. 
  * `released` is the original release date, formatted `M/D/YYYY`
  * `publisher` should match one of the existing publishers exactly. To add a new company, `Company` in `Company.kt` needs to be updated.
* The `image`, `timeline`, `description`, `series`, and `number` columns are optional
  * `image` should be added, just follow the same pattern as the other images. I host the images on my `github.io` page: `https://minirogue.github.io` and can add the images after the database is updated.
  * `timeline` roughly corresponds to the year in relation to the Battle of Yavin. Try not to let two distinct pieces of media share an exact timeline value. The decimal values exist to ensure a deterministic sorting for the timeline.
  * `description` should be kept as original as possible, if included. Descriptions from elsewhere may be subject to copyright.
  * `series` should be included for all comics, TPBs, TV media, and any novels that are part of a trilogy. There may be other cases where it is appropriate as well. Ensure that this column matches exactly for all entries from the same series.
  * `number` should correspond to issue number, episode number, volume number, etc. as appropriate. Should probably be included for anything that has a `series`.
* Keep the file sorted by `ID` when making a commit to avoid excessive diffs
* Increment the number in `Version.kt` by 1 to ensure the clients automatically update their local databases.
* Running the `:api:test` gradle task will test to ensure the CSV can be correctly loaded by the code. This should be run on the PR before it's merged, but that requires a manual trigger by me.

## Requested Features
Here are some things that I plan to add to the app:

* Add an import/export option so users can transfer or backup their list.
* Provide a "printable" output (probably a TXT) based on a user's current search.
* Add donation button, in case the users ever decide to feel generous. No plans to ever implement
  paid features. (note: this would probably violate some Google policy because they want their
  15-30%)
* Add Hardcover, Omnibus, and reference books to database.
* Expand support for different screen sizes.
* Add a "stats" tab so users can track completion ("110/380 owned" and such)

## Tech TODO

* Finish compartmentalizing code into modules
* Migrate code to a Kotlin multiplatform pattern

## License
TODO