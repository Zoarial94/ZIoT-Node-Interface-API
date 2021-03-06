package com.zoarial.TestAPI.controller;

import com.zoarial.iot.network.IoTPacketSectionList;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.zoarial.iot.threads.tcp.SocketHelper;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    String test() {
        return "This is a test!";
    }

    @GetMapping("/node/{uuid}/actions")
    String getNodeActions(@PathVariable("uuid") String uuidStr) throws Exception {

        return "Here are all the actions from " + uuidStr + ": ";
    }

    @GetMapping("/action/{uuid}")
    String getActionFromId(@PathVariable("uuid") String uuidStr) throws Exception {
        UUID uuid = UUID.fromString(uuidStr);

        SocketHelper socketHelper;
        try {
            socketHelper = new SocketHelper(new Socket("localhost", 9494));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Could not make connection");
        }
        IoTPacketSectionList packetSectionList = new IoTPacketSectionList();

        // Header
        packetSectionList.add("ZIoT");
        // Version
        packetSectionList.add((byte)0);
        // Session ID
        int sessionID = (int)(Math.random() * Integer.MAX_VALUE);
        System.out.println("Created SessionID: " + sessionID);
        packetSectionList.add(sessionID);
        packetSectionList.add("info");
        packetSectionList.add("action");
        packetSectionList.add(uuid);

        socketHelper.out.write(packetSectionList.getNetworkResponse());
        socketHelper.out.flush();

        if(!Arrays.equals("ZIoT".getBytes(), socketHelper.in.readNBytes(4))) {
            throw new Exception("Was not ZIoT response");
        }

        System.out.println("Session ID: " + socketHelper.readInt());

        byte status = socketHelper.readByte();

        // If 0, then there was an error
        if(status == (byte)0) {
            return socketHelper.readString();
        } else if(status != (byte)1) {
            return "Error: expected a success or failure.";
        }

        String json = socketHelper.readJson();

        try {
            socketHelper.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }

        //JSONObject root = new JSONObject(json);

        return json;
    }

    @PostMapping(value = "/action/{uuid}/run", produces = {MediaType.TEXT_PLAIN_VALUE})
    String runAction(@PathVariable("uuid") String uuidStr, @RequestParam Map<String, String> allParams) throws Exception {
        UUID uuid = UUID.fromString(uuidStr);

        System.out.println("Requested to run uuid: " + uuid);

        SocketHelper socketHelper;
        try {
            socketHelper = new SocketHelper(new Socket("localhost", 9494));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Could not make connection");
        }
        IoTPacketSectionList packetSectionList = new IoTPacketSectionList();

        // Header
        packetSectionList.add("ZIoT");
        // Version
        packetSectionList.add((byte)0);
        // Session ID
        int sessionID = (int)(Math.random() * Integer.MAX_VALUE);
        System.out.println("Created SessionID: " + sessionID);
        packetSectionList.add(sessionID);
        packetSectionList.add("action");
        packetSectionList.add(uuid);

        JSONObject root = new JSONObject();
        JSONArray jsonArgs = new JSONArray();
        root.put("args", jsonArgs);

        for(Map.Entry<String, String> e : allParams.entrySet()) {
            System.out.println(e.getKey() + ": \"" + e.getValue() + "\"");
            jsonArgs.put(e.getValue());
        }

        packetSectionList.add(root.toString());

        socketHelper.out.write(packetSectionList.getNetworkResponse());
        socketHelper.out.flush();

        if(!Arrays.equals("ZIoT".getBytes(), socketHelper.in.readNBytes(4))) {
            throw new Exception("Was not ZIoT response");
        }

        System.out.println("Session ID: " + socketHelper.readInt());

//        byte status = socketHelper.readByte();
//
//        // If 0, then there was an error
//        if(status == (byte)0) {
//            return socketHelper.readString();
//        } else if(status != (byte)1) {
//            return "Error: expected a success or failure.";
//        }

        String response = socketHelper.readString();

        try {
            socketHelper.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }

        return response;
    }

    @PostMapping("/action/{uuid}/updateInfo")
    ResponseEntity<String> updateAction(@PathVariable("uuid") String uuidStr, @RequestParam Map<String, String> allParams) throws Exception {
        UUID uuid = UUID.fromString(uuidStr);

        System.out.println("Requested to update uuid: " + uuid);

        SocketHelper socketHelper;
        try {
            socketHelper = new SocketHelper(new Socket("localhost", 9494));
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("No action with UUID: " + uuid, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        IoTPacketSectionList packetSectionList = new IoTPacketSectionList();

        // Header
        packetSectionList.add("ZIoT");
        // Version
        packetSectionList.add((byte)0);
        // Session ID
        int sessionID = (int)(Math.random() * Integer.MAX_VALUE);

        System.out.println("Created SessionID: " + sessionID);
        packetSectionList.add(sessionID);
        packetSectionList.add("info");
        packetSectionList.add("action");
        packetSectionList.add(uuid);

        socketHelper.out.write(packetSectionList.getNetworkResponse());
        socketHelper.out.flush();

        if(!Arrays.equals("ZIoT".getBytes(), socketHelper.in.readNBytes(4))) {
            throw new Exception("Was not ZIoT response");
        }

        System.out.println("Session ID: " + socketHelper.readInt());

        // If 0, then there was an error
        if(socketHelper.readByte() == 0) {
            return new ResponseEntity<>("Error: expected a success or failure.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JSONObject json = new JSONObject(socketHelper.readJson());
        System.out.println(json);

        for(var entity : allParams.entrySet()) {
            log.trace("Found: " + entity.getKey() + ":" + entity.getValue());
            packetSectionList.clear();
            // Header
            packetSectionList.add("ZIoT");
            // Version
            packetSectionList.add((byte)0);
            // Session ID
            sessionID = (int)(Math.random() * Integer.MAX_VALUE);

            System.out.println("Created SessionID: " + sessionID);
            packetSectionList.add(sessionID);
            packetSectionList.add("updateAction");
            packetSectionList.add(uuid);
            packetSectionList.add(entity.getKey());
            switch(entity.getKey()) {
                case "securityLevel" -> packetSectionList.add(Byte.parseByte(entity.getValue()));
                case "encrypt", "local" -> packetSectionList.add(Boolean.parseBoolean(entity.getValue()));
                case "desc" -> {
                    String tmp = entity.getValue();
                    if(tmp.isBlank()) {
                        System.out.println("Skipping...");
                        continue;
                    }
                    packetSectionList.add(entity.getValue());
                }
                default -> {
                    System.out.println("I shouldn't be here!!!");
                    continue;
                }
            }


            socketHelper.out.write(packetSectionList.getNetworkResponse());
            socketHelper.out.flush();

            byte[] maybeHeader = socketHelper.in.readNBytes(4);
            if(!Arrays.equals("ZIoT".getBytes(), maybeHeader)) {
                throw new Exception("Was not ZIoT response");
            }

            System.out.println("Session ID: " + socketHelper.readInt());

            if(!socketHelper.readString().equals("Success.")) {
                System.out.println("Unexpected error.");
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

        }


        try {
            socketHelper.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping("/actions")
    String getActions() throws Exception {
        SocketHelper socketHelper;
        try {
            socketHelper = new SocketHelper(new Socket("localhost", 9494));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Could not make connection");
        }
        IoTPacketSectionList packetSectionList = new IoTPacketSectionList();

        // Header
        packetSectionList.add("ZIoT");
        // Version
        packetSectionList.add((byte)0);
        // Session ID
        packetSectionList.add((int)(Math.random() * Integer.MAX_VALUE));
        packetSectionList.add("info");
        packetSectionList.add("actions");

        socketHelper.out.write(packetSectionList.getNetworkResponse());
        socketHelper.out.flush();

        if(!Arrays.equals("ZIoT".getBytes(), socketHelper.in.readNBytes(4))) {
            throw new Exception("Was not ZIoT response");
        }

        System.out.println("Session ID: " + socketHelper.readInt());
        int numberOfActions = socketHelper.readInt();
        System.out.println("Number of Actions: " + numberOfActions);

        JSONArray actions = new JSONArray();

        for(int i = 0; i < numberOfActions; i++) {
            JSONObject action = new JSONObject();
            action.put("UUID", socketHelper.readUUID());
            action.put("nodeUUID", socketHelper.readUUID());
            action.put("name", socketHelper.readString());
            action.put("securityLevel", socketHelper.readByte());
            action.put("args", socketHelper.readByte());
            action.put("encrypt", socketHelper.readBoolean());
            action.put("local", socketHelper.readBoolean());
            actions.put(action);
        }

        try {
            socketHelper.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }

        JSONObject root = new JSONObject();
        root.put("actions", actions);

        return root.toString();
    }


    @GetMapping("/nodes")
    String getNodes() throws Exception {
        SocketHelper socketHelper;
        try {
            socketHelper = new SocketHelper(new Socket("localhost", 9494));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("Could not make connection");
        }
        IoTPacketSectionList packetSectionList = new IoTPacketSectionList();

        // Header
        packetSectionList.add("ZIoT");
        // Version
        packetSectionList.add((byte)0);
        // Session ID
        packetSectionList.add((int)(Math.random() * Integer.MAX_VALUE));
        packetSectionList.add("info");
        packetSectionList.add("nodes");

        socketHelper.out.write(packetSectionList.getNetworkResponse());
        socketHelper.out.flush();

        if(!Arrays.equals("ZIoT".getBytes(), socketHelper.in.readNBytes(4))) {
            throw new Exception("Was not ZIoT response");
        }

        System.out.println("Session ID: " + socketHelper.readInt());
        int numberOfNodes = socketHelper.readInt();
        System.out.println("Number of Nodes: " + numberOfNodes);

        JSONArray nodes = new JSONArray();

        for(int i = 0; i < numberOfNodes; i++) {
            JSONObject node = new JSONObject();
            node.put("UUID", socketHelper.readUUID());
            node.put("hostname", socketHelper.readString());
            node.put("node_type", socketHelper.readByte());
            node.put("last_heard_from", socketHelper.readLong());
            nodes.put(node);
        }

        try {
            socketHelper.close();
        } catch (IOException ex){
            ex.printStackTrace();
        }

        JSONObject root = new JSONObject();
        root.put("nodes", nodes);

        return root.toString();
    }
}
