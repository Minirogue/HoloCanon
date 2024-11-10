package com.minirogue.starwarscanontracker.core.model

import com.minirogue.common.model.StarWarsMedia
import com.minirogue.media.notes.model.MediaNotes

data class MediaAndNotes(val mediaItem: StarWarsMedia, val notes: MediaNotes)
