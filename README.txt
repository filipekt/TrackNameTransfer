WHAT THE PROGRAM DOES AND WHY IT MIGHT BE USEFUL: 

Recently, when I was sorting through my digital music library, I stumbled upon
an issue with file naming. I wanted to re-rip some of my music CDs, because the
old ripped files were in MP3 format and I wanted them in a lossless format such
as FLAC. However, when re-ripping the CDs, I didn't want to enter the elaborate
track names again by hand, as originally with the MP3s. I wanted to use the
track names from the older MP3 rip, because they were carefully transcripted
from the booklet and moreover followed my personal preference. This application
provides a way how to transfer the track names of the original MP3s to the newly
ripped files. Sometimes this application is not needed - for instance in the
cases when the CD info can be loaded from an online CD database. However,
especially in the case of classical music CDs, where the track names can get
quite structured and complicated, the track names offered by online CD databases
can often be unsatisfactory. After this application provides the track names, it
is trivial to use any of the ID3Tag editors to transfer the track names to the
 actual ID3Tags.

WHAT IT DOES EXACTLY:

The application takes two directories as input - the SOURCE and TARGET. SOURCE
contains the original rip of a music CD, in any of the common music file
formats. Any other non-music files are ignored by the application. TARGET
contains the new rip of the same music CD, in any of the common music file
formats. The program assumes (!!!) that the filenames of music files in both
directories match the following pattern: TRACK_NUMBER TRACK_NAME.EXTENSION,
where TRACK_NUMBER is the number of the music track on the original CD,
TRACK_NAME is the name of the track, and EXTENSION is a filename extension. The
program then pairs the corresponding tracks from both directories, according to
their respective TRACK_NUMBER values, and renames the file from TARGET
directory, so that its TRACK_NAME is the same as the one from SOURCE directory.

EXAMPLE:

MP3_RIP_DIR:
	01 [Haydn] Klavierkonzert D-dur Hob.XVIII-11 - I.Vivace.mp3
	02 II.Un poco adagio.mp3
	03 III.Rondo all'ungherese.mp3
	04 [Haydn] Klavierkonzert G-dur Hob.XVIII-4 - I.Allegro.mp3
	05 II.Adagio cantabile (Cadenza - Nino Rota).mp3
	06 III.Rondo - Presto.mp3
FLAC_RIP_DIR:
	01 TRACK.flac
	02 TRACK.flac
	03 TRACK.flac
	04 TRACK.flac
	05 TRACK.flac
	06 TRACK.flac

The application gets MP3_RIP_DIR as SOURCE directory and FLAC_RIP_DIR as TARGET
 directory. While it does not do any changes in the SOURCE dir, it renames the
 files in TARGET dir in the following way:

FLAC_RIP_DIR:
 	01 [Haydn] Klavierkonzert D-dur Hob.XVIII-11 - I.Vivace.flac
	02 II.Un poco adagio.flac
	03 III.Rondo all'ungherese.flac
	04 [Haydn] Klavierkonzert G-dur Hob.XVIII-4 - I.Allegro.flac
	05 II.Adagio cantabile (Cadenza - Nino Rota).flac
	06 III.Rondo - Presto.flac


REQUIREMENTS:

JDK 8 update 40 or later 