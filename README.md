# Twitter4Spigot
Twitter for Spigot

## How to use
1. Put the plugin in your plugins folder
2. Restart your server
3. Add a Twitter Subscriber with `/t4s subscriber add <Mode> <Tag>` (see 'Modes & tag' for an explanation)
4. Add a Information Holder with `/t4s subscriber edit <Sub Id> createInformationHolder` (see 'Information Holders' for an explanation)
5. Configure the information holders

## Modes & tag
The mode specifies what kind of tweets should be pulled. There are 4 different modes: USER_RECENT, USER_POPULAR, HASHTAG_RECENT and HASHTAG_POPULAR.

#### USER_RECENT
Displays the most recent tweet of the user specified in the tag.

#### USER_POPULAR
Displays a popular tweet of the user specified in the tag.

#### HASHTAG_RECENT
Displays the most recent tweet of the hashtag specified in the tag.

#### HASHTAG_POPULAR
Displays a popular tweet of the hashtag specified in the tag.

## Information Holders
A information holder consists of 5 holograms: A tweet hologram, a author hologram, a like hologram, a retweet hologram and a comment hologram.
The tweet hologram displays the tweet, the author hologram displays the author, the like hologram displays the like count, the retweet hologram displays the retweet count and the comment hologram displays the comment count.
The holograms can be set with `/t4s edit <Sub Id> editInformationHolder <I.h. Id> set...` (e.g. `/t4s edit 3 editInformationHolder 0 setTweetHolo`)