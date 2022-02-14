package com.hakai.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.hakai.command.Command;
import com.hakai.utils.ItemUtils;
import com.hakai.utils.MessageUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.sandrohc.schematic4j.SchematicUtil;
import net.sandrohc.schematic4j.schematic.Schematic;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class StructHolo extends Command {

    public StructHolo() {
        super("structholo", "yss|schem <file> <is-small>");
    }

    private final static SimpleCommandExceptionType HOLOGRAM_FILE_NOT_FOUND = new SimpleCommandExceptionType(new LiteralText("File not found."));
    private final static SimpleCommandExceptionType HOLOGRAM_FILE_ERROR = new SimpleCommandExceptionType(new LiteralText("Structure Syntax incorrect."));
    private final static SimpleCommandExceptionType HOLOGRAM_STILL_ACTIVE = new SimpleCommandExceptionType(new LiteralText("Structure or Image Hologram still active."));

    private static ItemStack lastHologramItem = null;

    public static ItemStack[] items = null;
    private static int index = -1;

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("cancel").executes(context -> {
            index = 0;
            items = null;
            lastHologramItem = null;
            MessageUtils.printChatMessageWithPrefix("§aStrukturen Hologram §cabgebrochen!");
            return SINGLE_SUCCESS;
        })).then(literal("schematic").then(argument("file", StringArgumentType.string()).then(argument("small", BoolArgumentType.bool()).executes(context -> {
            if(items != null || ImageHologramCommand.items != null)
                throw HOLOGRAM_STILL_ACTIVE.create();

            try {
                Path path = Paths.get(StringArgumentType.getString(context, "file"));
                Schematic schem = SchematicUtil.load(path);

                double armorStandDistance = 0.63;
                if(BoolArgumentType.getBool(context, "small"))
                    armorStandDistance = 0.45;

                ArrayList<ItemStack> holoItems = new ArrayList<ItemStack>();
                for(int y = 0; y < schem.getHeight(); y++){
                    for(int z = 0; z < schem.getLength(); z++){
                        for(int x = 0; x < schem.getWidth(); x++){
                            if(!schem.getBlock(x, y, z).name.contains("air")){
                                double ax = MinecraftClient.getInstance().player.getX() + (armorStandDistance * x);
                                double ay = MinecraftClient.getInstance().player.getY() + (armorStandDistance * y);
                                double az = MinecraftClient.getInstance().player.getZ() + (armorStandDistance * z);
                                holoItems.add(createHologram(schem.getBlock(x, y, z).name, ax, ay, az, BoolArgumentType.getBool(context, "small"), "", ""));
                            }
                        }
                    }
                }
                index = 0;
                items = new ItemStack[holoItems.size()];
                holoItems.toArray(items);
                nextItem(-1);

                MessageUtils.printChatMessageWithPrefix("§aSchematic '" + schem.getName() + "' wurde geladen.");
                return SINGLE_SUCCESS;
            } catch (FileNotFoundException e) {
                throw HOLOGRAM_FILE_NOT_FOUND.create();
            }catch (Exception e){
                e.printStackTrace();
                throw HOLOGRAM_FILE_ERROR.create();
            }
        })))).then(literal("hs").then(argument("file", StringArgumentType.string()).then(argument("small", BoolArgumentType.bool()).executes(context -> {
            if (items == null && ImageHologramCommand.items == null) {
                try {
                    File file = new File(MinecraftClient.getInstance().runDirectory, "yellowsnow/structures/" + StringArgumentType.getString(context, "file") + ".hs");
                    Scanner scanner = new Scanner(file);
                    double armorStandDistance = 0.63D;
                    if (BoolArgumentType.getBool(context, "small")) {
                        armorStandDistance = 0.45D;
                    }

                    ArrayList holoItems = new ArrayList();
                    while(scanner.hasNextLine()) {
                        String[] blockInfo = scanner.nextLine().split(",");
                        if (blockInfo.length >= 4 && !blockInfo[0].startsWith("#")) {
                            double x = MinecraftClient.getInstance().player.getX() + armorStandDistance * Double.parseDouble(blockInfo[1]);
                            double y = MinecraftClient.getInstance().player.getY() + armorStandDistance * Double.parseDouble(blockInfo[2]);
                            double z = MinecraftClient.getInstance().player.getZ() + armorStandDistance * Double.parseDouble(blockInfo[3]);
                            String name = "";
                            String tag = "";
                            if (blockInfo.length >= 5)  name = blockInfo[4];
                            if (blockInfo.length >= 6)  tag = blockInfo[5];

                            holoItems.add(this.createHologram(blockInfo[0], x, y, z, BoolArgumentType.getBool(context, "small"), name, tag));
                        }
                    }
                    index = 0;
                    items = new ItemStack[holoItems.size()];
                    holoItems.toArray(items);
                    nextItem(-1);

                    return SINGLE_SUCCESS;
                } catch (FileNotFoundException e) {
                    throw HOLOGRAM_FILE_NOT_FOUND.create();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw HOLOGRAM_FILE_ERROR.create();
                }
            } else {
                throw HOLOGRAM_STILL_ACTIVE.create();
            }
        }))));
    }

    private ItemStack createHologram(String block, double x, double y, double z, boolean small, String name, String nbt) {
        ItemStack itemstack = new ItemStack(Items.ARMOR_STAND, 1);
        try {
            String tag = "{display:{Name:'{\"text\":\"Hologram\",\"color\":\"yellow\",\"italic\":false}'},EntityTag:{Pos:[" + x + "," + y + "," + z + "], Rotation:[0, 0],NoGravity:1b,NoBasePlate:1b,Invisible:1b,Pose:{Body:[0f,0f,0f],Head:[0f,0f,0f]},DisabledSlots:4144959,ArmorItems:[{},{},{},{id:\"" + block + "\",Count:1b}]";
            if(small)
                tag += ",Small:1b";
            if(!name.isEmpty()){
                String jsonText = Text.Serializer.toJson(MessageUtils.toSingleText(MessageUtils.toLiteralText(name)));
                tag += ",CustomNameVisible:1b,CustomName:'" + jsonText.replace("&", "§") + "'";
            }
            tag += nbt;
            itemstack.setNbt(StringNbtReader.parse(tag + "}}"));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return itemstack;
    }

    public static void usedItem(ItemStack itemstack, int slot) {
        if(itemstack == null || lastHologramItem == null || !itemstack.hasNbt() || !Objects.equals(itemstack.getNbt(), lastHologramItem.getNbt()))
            return;
        nextItem(slot);
    }

    private static void nextItem(int slot) {
        try {
            ItemUtils.checkCreative();
        } catch (CommandSyntaxException e) { return; }
        if(index >= items.length) {
            index = 0;
            items = null;
            lastHologramItem = null;
            ItemUtils.giveItem(slot, ItemStack.EMPTY);
            MessageUtils.printChatMessageWithPrefix("§aAlle Armorstands Platziert!");
            return;
        }
        MessageUtils.actionBar("§aNoch §7" + (items.length - index) + " §aArmorstands übrig.");
        lastHologramItem = items[index];
        ItemUtils.giveItem(slot, lastHologramItem);
        index++;
    }

    public static void initFolder(){
        File file = new File(MinecraftClient.getInstance().runDirectory, "yellowsnow/structures");
        file.mkdirs();
    }
}
