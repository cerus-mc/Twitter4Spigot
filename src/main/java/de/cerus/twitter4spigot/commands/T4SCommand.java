/*
 *  Copyright (c) 2018 Cerus
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Cerus
 *
 */

package de.cerus.twitter4spigot.commands;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import de.cerus.ceruslib.commandframework.Arguments;
import de.cerus.ceruslib.commandframework.Command;
import de.cerus.twitter4spigot.twitter.InformationHolder;
import de.cerus.twitter4spigot.twitter.SubscriberController;
import de.cerus.twitter4spigot.twitter.TwitterSubscriber;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class T4SCommand extends Command {

    private SubscriberController subscriberController;

    public T4SCommand(JavaPlugin plugin, SubscriberController subscriberController) {
        super(plugin, "twitter4spigot");
        this.subscriberController = subscriberController;

        setAliases(Collections.singletonList("t4s"));
        setPrefix("§3§lT§b§l4§3§lS §8§l| §f");
        setNoPermsMessage("§cYou do not have the required permission(s): §e");
    }

    @Override
    public boolean onPlayerCommand(Player player, org.bukkit.command.Command command, String s, Arguments arguments) {
        if(!checkPermission(player, "t4s.use"))
            return true;

        if (arguments.size() == 2) {
            if (arguments.get(0).equalsIgnoreCase("subscriber")) {
                if (arguments.get(1).equalsIgnoreCase("list")) {
                    player.sendMessage("§8§m-------------------------------------------------------------");
                    player.sendMessage("§3§lS§b§lubscribers: §f" + subscriberController.getSubscribers().size());
                    subscriberController.getSubscribers().forEach(subscriber -> player.sendMessage("§7- §e" +
                            subscriber.getId() + " [" + subscriber.getMode().toString() + ", " + subscriber.getAdditionalInfo() + "]"));
                    player.sendMessage("§8§m-------------------------------------------------------------");
                    return true;
                }
            }
        }

        if (arguments.size() == 3) {
            if (arguments.get(0).equalsIgnoreCase("subscriber")) {
                if (arguments.get(1).equalsIgnoreCase("remove")) {
                    int id;
                    try {
                        id = arguments.get(2).toInt();
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(getPrefix() + "§cInvalid number!");
                        return true;
                    }

                    TwitterSubscriber subscriber = subscriberController.getSubscriber(id);
                    if (subscriber == null) {
                        player.sendMessage(getPrefix() + "§cInvalid id!");
                        return true;
                    }

                    subscriber.getInformationHolders().forEach(informationHolder -> {
                        if (informationHolder.getAuthorHolder() != null)
                            informationHolder.getAuthorHolder().delete();
                        if (informationHolder.getTweetHolder() != null)
                            informationHolder.getTweetHolder().delete();
                        if (informationHolder.getLikeHolder() != null)
                            informationHolder.getLikeHolder().delete();
                        if (informationHolder.getRetweetHolder() != null)
                            informationHolder.getRetweetHolder().delete();
                        if (informationHolder.getCommentHolder() != null)
                            informationHolder.getCommentHolder().delete();
                    });
                    subscriberController.removeSubscriber(subscriber);
                    player.sendMessage(getPrefix() + "§aSubscriber removed!");
                    return true;
                }
                if (arguments.get(1).equalsIgnoreCase("info")) {
                    int id;
                    try {
                        id = arguments.get(2).toInt();
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(getPrefix() + "§cInvalid number!");
                        return true;
                    }

                    TwitterSubscriber subscriber = subscriberController.getSubscriber(id);
                    if (subscriber == null) {
                        player.sendMessage(getPrefix() + "§cInvalid id!");
                        return true;
                    }

                    player.sendMessage("§8§m-------------------------------------------------------------");
                    player.sendMessage("9ID: §f" + subscriber.getId());
                    player.sendMessage("§7Mode: §f" + subscriber.getMode().toString());
                    player.sendMessage("§7Tag: §f" + subscriber.getAdditionalInfo());
                    player.sendMessage("§7Information Holders: §f" + subscriber.getInformationHolders().size());
                    player.sendMessage("§8§m-------------------------------------------------------------");
                    return true;
                }
            }
        }

        if (arguments.size() == 4) {
            if (arguments.get(1).equalsIgnoreCase("add")) {
                String _mode = arguments.get(2).getArgument();
                TwitterSubscriber.Mode mode;
                try {
                    mode = TwitterSubscriber.Mode.valueOf(_mode);
                } catch (Exception ignored) {
                    player.sendMessage(getPrefix() + "§cInvalid mode! Please choose between " +
                            Arrays.stream(TwitterSubscriber.Mode.values()).map(Enum::toString)
                                    .collect(Collectors.joining(", ")) + "!");
                    return true;
                }

                String tag = arguments.get(3).getArgument();

                TwitterSubscriber subscriber = new TwitterSubscriber(-1, mode, tag, new ArrayList<>());
                subscriberController.addSubscriber(subscriber);
                player.sendMessage(getPrefix() + "§aSubscriber (#" + subscriber.getId() + ") added!");
                return true;
            }
            if (arguments.get(1).equalsIgnoreCase("edit")) {
                if (arguments.get(3).equalsIgnoreCase("createInformationHolder")) {
                    int id;
                    try {
                        id = arguments.get(2).toInt();
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(getPrefix() + "§cInvalid number!");
                        return true;
                    }

                    TwitterSubscriber subscriber = subscriberController.getSubscriber(id);
                    List<InformationHolder> informationHolders = subscriber.getInformationHolders();
                    informationHolders.add(new InformationHolder(informationHolders.size(), null,
                            null, null, null, null));
                    subscriberController.save();
                    player.sendMessage(getPrefix() + "§aInformation Holder (#" + (informationHolders.size() - 1) + ") created!");
                    return true;
                }
                return true;
            }
        }

        if (arguments.size() == 6) {
            if (arguments.get(1).equalsIgnoreCase("edit")) {
                if (arguments.get(3).equalsIgnoreCase("editInformationHolder")) {
                    int subId;
                    try {
                        subId = arguments.get(2).toInt();
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(getPrefix() + "§cInvalid number!");
                        return true;
                    }

                    int ihId;
                    try {
                        ihId = arguments.get(4).toInt();
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(getPrefix() + "§cInvalid number!");
                        return true;
                    }

                    TwitterSubscriber subscriber = subscriberController.getSubscriber(subId);
                    if (subscriber == null) {
                        player.sendMessage(getPrefix() + "§cInvalid subscriber id!");
                        return true;
                    }

                    InformationHolder holder = subscriber.getInformationHolders().stream()
                            .filter(informationHolder -> informationHolder.getId() == ihId).findAny().orElse(null);
                    if(holder == null) {
                        player.sendMessage(getPrefix() + "§cInvalid information holder id!");
                        return true;
                    }

                    if(arguments.get(5).equalsIgnoreCase("setTweetHolo")) {
                        Hologram hologram = HologramsAPI.createHologram(getPlugin(), player.getLocation());
                        hologram.appendTextLine("Tweet holder");
                        holder.setTweetHolder(hologram);
                        subscriberController.save();
                        player.sendMessage(getPrefix()+"§aTweet hologram set!");
                        return true;
                    }
                    if(arguments.get(5).equalsIgnoreCase("setLikeHolo")) {
                        Hologram hologram = HologramsAPI.createHologram(getPlugin(), player.getLocation());
                        hologram.appendTextLine("Like holder");
                        holder.setLikeHolder(hologram);
                        subscriberController.save();
                        player.sendMessage(getPrefix()+"§aLike hologram set!");
                        return true;
                    }
                    if(arguments.get(5).equalsIgnoreCase("setRetweetHolo")) {
                        Hologram hologram = HologramsAPI.createHologram(getPlugin(), player.getLocation());
                        hologram.appendTextLine("Retweet holder");
                        holder.setRetweetHolder(hologram);
                        subscriberController.save();
                        player.sendMessage(getPrefix()+"§aRetweet hologram set!");
                        return true;
                    }
                    if(arguments.get(5).equalsIgnoreCase("setCommentHolo")) {
                        Hologram hologram = HologramsAPI.createHologram(getPlugin(), player.getLocation());
                        hologram.appendTextLine("Comment holder");
                        holder.setCommentHolder(hologram);
                        subscriberController.save();
                        player.sendMessage(getPrefix()+"§aComment hologram set!");
                        return true;
                    }
                    if(arguments.get(5).equalsIgnoreCase("setAuthorHolo")) {
                        Hologram hologram = HologramsAPI.createHologram(getPlugin(), player.getLocation());
                        hologram.appendTextLine("Author holder");
                        holder.setAuthorHolder(hologram);
                        subscriberController.save();
                        player.sendMessage(getPrefix()+"§aAuthor hologram set!");
                        return true;
                    }

                    player.sendMessage("§8§m-------------------------------------------------------------");
                    player.sendMessage("§e/t4s subscriber edit <Sub. Id> editInformationHolder <I.h. Id> setTweetHolo");
                    player.sendMessage("§e/t4s subscriber edit <Sub. Id> editInformationHolder <I.h. Id> setLikeHolo");
                    player.sendMessage("§e/t4s subscriber edit <Sub. Id> editInformationHolder <I.h. Id> setRetweetHolo");
                    player.sendMessage("§e/t4s subscriber edit <Sub. Id> editInformationHolder <I.h. Id> setCommentHolo");
                    player.sendMessage("§e/t4s subscriber edit <Sub. Id> editInformationHolder <I.h. Id> setAuthorHolo");
                    player.sendMessage("§8§m-------------------------------------------------------------");
                    return true;
                }
            }
        }

        player.sendMessage("§8§m-------------------------------------------------------------");
        player.sendMessage("§3§lT§b§lwitter §3§lF§b§lor §3§lS§b§lpigot §7v" + getPlugin().getDescription().getVersion());
        player.sendMessage(" ");
        player.sendMessage("§e/t4s subscriber list");
        player.sendMessage("§e/t4s subscriber add <Mode> <Tag>");
        player.sendMessage("§e/t4s subscriber remove <Sub. Id>");
        player.sendMessage("§e/t4s subscriber info <Sub. Id>");
        player.sendMessage("§e/t4s subscriber edit <Sub. Id> createInformationHolder");
        player.sendMessage("§e/t4s subscriber edit <Sub. Id> editInformationHolder <I.h. Id> setTweetHolo");
        player.sendMessage("§e/t4s subscriber edit <Sub. Id> editInformationHolder <I.h. Id> setLikeHolo");
        player.sendMessage("§e/t4s subscriber edit <Sub. Id> editInformationHolder <I.h. Id> setRetweetHolo");
        player.sendMessage("§e/t4s subscriber edit <Sub. Id> editInformationHolder <I.h. Id> setCommentHolo");
        player.sendMessage("§e/t4s subscriber edit <Sub. Id> editInformationHolder <I.h. Id> setAuthorHolo");
        player.sendMessage("§8§m-------------------------------------------------------------");
        return true;
    }

/*    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<String> list = new ArrayList<>();
        if(args.length == 0) {
            list.add("subscriber");
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("subscriber")) {
                list.add("list");
                list.add("add");
                list.add("remove");
                list.add("info");
                list.add("edit");
            }
        } else if(args.length == 2) {
            if(args[1].equalsIgnoreCase("add")) {
                list.add("<Mode> <Tag>");
            } else if(args[1].equalsIgnoreCase("remove") ||
                    args[1].equalsIgnoreCase("info") ||
                    args[1].equalsIgnoreCase("edit")) {
                list.add("<Sub. Id>");
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("add")) {
                list.add("<Mode> <Tag>");
            } else if(args[0].equalsIgnoreCase("remove") ||
                    args[0].equalsIgnoreCase("info") ||
                    args[0].equalsIgnoreCase("edit")) {
                list.add("<Sub. Id>");
            }
        }
        return list;
    }*/
}
