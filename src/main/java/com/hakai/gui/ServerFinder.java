package com.hakai.gui;

import com.hakai.main.HakaiClient;
import com.hakai.tabui.TabUI;
import com.hakai.utils.MathUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServerFinder extends Screen {
    private MultiplayerScreen prevScreen;
    private static boolean scanning = false;
    private static int checked = 0;
    private static int successfull = 0;
    private static String status = "waiting...";

    private ButtonWidget scanButton;
    private TextFieldWidget ipInput;
    private TextFieldWidget maxThreadInput;

    public ServerFinder(MultiplayerScreen prevScreen) {
        super(Text.of("Server Finder"));
        this.prevScreen = prevScreen;
    }

    @Override
    protected void init() {
        addDrawableChild(scanButton = new ButtonWidget(width / 2 - 100, height / 4 + 96 + 12, 200, 20, Text.of("Search"), b -> searchOrCancel()));
        addDrawableChild(new ButtonWidget(width / 2 - 100, height / 4 + 144 + 12, 200, 20, Text.of("Back"), b -> client.setScreen(prevScreen)));

        ipInput = new TextFieldWidget(textRenderer, width / 2 - 100, height / 4 + 34, 200, 20, Text.of(""));
        ipInput.setMaxLength(200);
        ipInput.setTextFieldFocused(true);
        addDrawableChild(ipInput);
        maxThreadInput = new TextFieldWidget(textRenderer, width / 2 - 32, height / 4 + 58, 26, 12, Text.of(""));
        maxThreadInput.setMaxLength(3);
        maxThreadInput.setText("128");
        addDrawableChild(maxThreadInput);
    }

    @Override
    public void tick() {
        scanButton.setMessage(Text.of(scanning ? "Cancel" : "Search"));
        ipInput.active = !scanning;
        maxThreadInput.active = !scanning;
        scanButton.active = MathUtils.isInteger(maxThreadInput.getText()) && !ipInput.getText().isEmpty();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        textRenderer.draw(matrices, "Server address:", width / 2 - 100, height / 4 + 24, TabUI.textRgb ? HakaiClient.getInstance().getRGB() : TabUI.textColor);
        textRenderer.draw(matrices, "Max. threads:", width / 2 - 100, height / 4 + 60, TabUI.textRgb ? HakaiClient.getInstance().getRGB() : TabUI.textColor);
        textRenderer.draw(matrices, "Checked: " + checked + " / 1792", width / 2 - 100, height / 4 + 84, TabUI.textRgb ? HakaiClient.getInstance().getRGB() : TabUI.textColor);
        textRenderer.draw(matrices, "Working: " + successfull + " / " + checked, width / 2 - 100, height / 4 + 94, TabUI.textRgb ? HakaiClient.getInstance().getRGB() : TabUI.textColor);
        textRenderer.draw(matrices, "Status: " + status, width / 2 - 100, height / 4 + 134, TabUI.textRgb ? HakaiClient.getInstance().getRGB() : TabUI.textColor);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void searchOrCancel() {
        if(scanning) {
            scanning = false;
            status = "canceled";
            return;
        }
        checked = 0;
        successfull = 0;
        new Thread(this::findServers, "Server Finder").start();
    }

    private void findServers() {
        int maxThreads = Integer.parseInt(maxThreadInput.getText());
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(ipInput.getText().split(":")[0].trim());
        } catch (UnknownHostException e) {
            status = "Unknown Host";
            e.printStackTrace();
            return;
        }

        status = "Preparing...";
        scanning = true;
        int[] ipParts = new int[4];
        for(int i = 0; i < 4; i++)
            ipParts[i] = addr.getAddress()[i] & 0xff;

        ArrayList<Pinger> pingers = new ArrayList<>();
        int[] changes = {0, 1, -1, 2, -2, 3, -3};
        for(int change : changes)
            for(int i2 = 0; i2 <= 255; i2++)
            {
                int[] ipParts2 = ipParts.clone();
                ipParts2[2] = ipParts[2] + change & 0xff;
                ipParts2[3] = i2;
                String ip = ipParts2[0] + "." + ipParts2[1] + "."
                        + ipParts2[2] + "." + ipParts2[3];

                Pinger pinger = new Pinger(ip, 25565);
                pinger.pingInThread();
                pingers.add(pinger);
                status = "Scanning...";
                while(pingers.size() >= maxThreads)
                {
                    if(!scanning) return;
                    updatePingers(pingers);
                }
            }
        while(pingers.size() > 0)
        {
            if(!scanning) return;
            updatePingers(pingers);
        }
        scanning = false;
        status = "Finished";
    }

    private void updatePingers(ArrayList<Pinger> pingers)
    {
        for(int i = 0; i < pingers.size(); i++) {
            if (pingers.get(i).isDone()) {
                checked++;
                if (pingers.get(i).isSuccessfull()) {
                    successfull++;
                    if (!isServerInList(pingers.get(i).getServerInfo().address)) {
                        prevScreen.getServerList().add(new ServerInfo("Hakai#" + successfull, pingers.get(i).getServerInfo().address, false));
                        prevScreen.getServerList().saveFile();
                    }
                }
                pingers.remove(i);
            }
        }
    }

    private boolean isServerInList(String ip)
    {
        for(int i = 0; i < prevScreen.getServerList().size(); i++)
            if(prevScreen.getServerList().get(i).address.equals(ip))
                return true;
        return false;
    }

    public static class Pinger {
        private ServerInfo serverInfo;
        private boolean isDone = false;
        private boolean isSuccessfull = false;

        public static void ping(String host){
            new Pinger(host, 25565).pingInThread();
        }

        public Pinger(String host, int port) {
            serverInfo = new ServerInfo("", host + ":" + port, false);
        }

        public void pingInThread(){
            Thread thread = new Thread(() -> {
                MultiplayerServerListPinger pinger = new MultiplayerServerListPinger();
                try {
                    pinger.add(serverInfo, () -> {});
                    isSuccessfull = true;
                    System.out.println("Server found: " + serverInfo.address);
                } catch (Exception e) {
                    System.out.println("Ping failed: " + serverInfo.address);
                }
                isDone = true;
            });
            thread.start();
        }

        public boolean isDone() {
            return isDone;
        }

        public boolean isSuccessfull() {
            return isSuccessfull;
        }

        public ServerInfo getServerInfo() {
            return serverInfo;
        }
    }
}


