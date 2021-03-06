package org.cyberpwn.react.network;

import org.cyberpwn.react.L;
import org.cyberpwn.react.util.GMap;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class RequestBasic extends Thread {
    private final RequestCallbackBasic callback;
    private final NetworkedServer ns;

    public RequestBasic(NetworkedServer ns, RequestCallbackBasic callback) {
        this.callback = callback;
        this.ns = ns;
    }

    @Override
    public void run() {
        try {
            Socket s = new Socket(ns.getAddress(), ns.getPort());
            s.setSoTimeout(500);
            DataInputStream i = new DataInputStream(s.getInputStream());
            DataOutputStream o = new DataOutputStream(s.getOutputStream());
            PacketRequest pr = new PacketRequest(ns.getUsername(), ns.getPassword(), PacketRequestType.GET_BASIC.toString());
            L.n("OUT: " + pr.toString());
            o.writeUTF(pr.toString());
            o.flush();
            String response = i.readUTF();
            PacketResponse ps = new PacketResponse(new JSONObject(response));
            L.n("IN: " + ps.toString());
            GMap<String, String> data = new GMap<>();

            if (ps.getString("type").equals("OK")) {
                for (String j : ps.getJSON().keySet()) {
                    if (j.equals("type")) {
                        continue;
                    }

                    try {
                        data.put(j, ps.getJSON().getString(j));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                callback.run(data, true);
            }

            callback.run(data, false);
        } catch (Exception ignored) {

        }
    }
}
