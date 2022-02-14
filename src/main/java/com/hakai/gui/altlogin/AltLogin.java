package com.hakai.gui.altlogin;

import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.hakai.mixin.MinecraftClientAccessor;
import com.hakai.utils.TheAlterning;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.apache.commons.compress.utils.IOUtils;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;

@Environment(EnvType.CLIENT)
public class AltLogin extends Screen {

    private final static String DEFAULT_LOGIN_REASON = "§7Benutze: <Email>:<Passwort> oder <TheAltening-Token>";
            private final static int DEFAULT_LOGIN_DELAY = 4*20;

            private int selectedSession = 0;
            private Session[] sessions;

            private ButtonWidget changeButton;
            private ButtonWidget loginButton;

            private int loginDelay = 0;

            private String usernameField = "";
            private String loginReason = DEFAULT_LOGIN_REASON;

            public AltLogin() {
                super(Text.of("Alt Login"));

                System.out.println(ClientBrandRetriever.getClientModName());
                sessions = new Session[0];

                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress("127.0.0.1", 3090), 500);
                    OutputStream out = socket.getOutputStream();
                    out.write("mcaccgettoken".getBytes());
                    out.flush();

                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    String username = input.readUTF();
                    String uuid = input.readUTF();
                    String accessToken = input.readUTF();
                    socket.close();

                    sessions = new Session[] {new Session(username, uuid, accessToken, MinecraftClient.getInstance().getSession().getXuid(), MinecraftClient.getInstance().getSession().getClientId(), Session.AccountType.MOJANG)};
                } catch(Exception e) {
                    sessions = new Session[0];
                    e.printStackTrace();
                }
            }

            @Override
            protected void init() {
                super.init();
                updateNameField();
                changeButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4, 200, 20, Text.of("Selected: None"), (buttonWidget) -> {
                    if(sessions.length <= 1)
                        return;
                    selectedSession++;
                    if(selectedSession >= sessions.length)
                        selectedSession = 0;
                    updateButtons();
                }, ButtonWidget.EMPTY));
                loginButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 24, 200, 20, Text.of("Login"), (buttonWidget) -> {
                    TheAlterning.switchAuth(TheAlterning.MOJANG);
                    ((MinecraftClientAccessor)this.client).setSession(sessions[selectedSession]);
                    updateNameField();
                }, ButtonWidget.EMPTY));
                loginButton.active = false;
                updateButtons();

                this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 24 + 24 + 24 + 10, 200, 20, Text.of("Clipboard Login"), (buttonWidget) -> {
                    String clipboard = MinecraftClient.getInstance().keyboard.getClipboard();
                    if(clipboard == null) {
                        setLoginReason("§cZwischenablage ist leer");
                    } else {
                        String email = "";
                        String password = "";
                        if(clipboard.endsWith("@alt.com")) {
                            TheAlterning.switchAuth(TheAlterning.THE_ALTERNING);
                            email = clipboard;
                            password = "1234";
                        } else {
                            TheAlterning.switchAuth(TheAlterning.MOJANG);
                            int index = clipboard.indexOf(':');
                            if(index <= 0) {
                                setLoginReason("§cFormat nicht erkannt... §7<Email>:<Passwort>");
                                return;
                            }
                            email = clipboard.substring(0, index);
                            password = clipboard.substring(index+1);
                        }

                        UserAuthentication auth = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
                        auth.setUsername(email);
                        auth.setPassword(password);

                        try {
                            auth.logIn();
                            ((MinecraftClientAccessor)this.client).setSession(new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), MinecraftClient.getInstance().getSession().getXuid(), MinecraftClient.getInstance().getSession().getClientId(), Session.AccountType.MOJANG));
                            updateNameField();
                        } catch (AuthenticationException e) {
                            if(clipboard.endsWith("@alt.com"))
                                setLoginReason("§cDer TheAltening Token falsch");
                            else
                               setLoginReason("§cEmail oder Passwort ist falsch");
                            e.printStackTrace();
                        }

                    }
                }, ButtonWidget.EMPTY));
            }

            @Override
            public void tick() {
                if(loginDelay == 1) {
                    loginReason = DEFAULT_LOGIN_REASON;
                    loginDelay = 0;
                } else if(loginDelay >= 1) {
                    loginDelay--;
                }
            }

            private String getUsernameByUUID(String uuid) {
                try {
                    String jsonResponse = new String(IOUtils.toByteArray(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names").openStream()));
                    String startOfName = "\"name\":\"";
                    int indexOf = jsonResponse.indexOf(startOfName);
                    if(indexOf != -1) {
                        String name = jsonResponse.substring(indexOf + startOfName.length());
                        indexOf = name.indexOf('\"');
                        if(indexOf != -1) {
                            name = name.substring(0, indexOf);
                            return name;
                        }
                    }
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

    private void updateButtons() {
        if(selectedSession >= sessions.length) {
            loginButton.active = false;
            return;
        }
        changeButton.setMessage(Text.of("Selected: " + sessions[selectedSession].getUsername()));
        loginButton.active = true;
    }

    private void setLoginReason(String loginReason) {
        this.loginDelay = DEFAULT_LOGIN_DELAY;
        this.loginReason = loginReason;
    }

    private void updateNameField() {
        String token = this.client.getSession().getAccessToken();
        usernameField = "§7Username: §a" + this.client.getSession().getUsername() + (isOnlineMode(token) ? "" : (" §8| §cCracked"));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, textRenderer, loginReason, this.width / 2, this.height / 4 + 24 + 24 + 20, Color.GRAY.getRGB());
        drawStringWithShadow(matrices, textRenderer, usernameField, 10, 10, Color.LIGHT_GRAY.getRGB());
        super.render(matrices, mouseX, mouseY, delta);
    }

    public static boolean isOnlineMode(String token) {
        return token.startsWith("ey") || token.startsWith("alt_");
    }

}