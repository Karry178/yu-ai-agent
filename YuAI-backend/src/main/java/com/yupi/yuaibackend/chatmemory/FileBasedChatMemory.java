package com.yupi.yuaibackend.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBasedChatMemory implements ChatMemory {

    // 定义存储路径
    private final String BASE_DIR;

    // 初始化一个Kryo对象（本身是线程安全的）
    private static final Kryo kryo = new Kryo();

    // 初始化时指定Kryo的动态初始化策略
    static {
        // 改为false,不用手动注册
        kryo.setRegistrationRequired(false);
        // 设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    // 构造对象时，指定文件保存目录
    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir;
        // 新建一个文件对象
        File baseDir = new File(dir);
        if (!baseDir.exists()) {
            // 如果该文件对象不存在，先创建出来，防止报错
            baseDir.mkdirs();
        }
    }


    /**
     * 增 - 插入一条消息
     * @param conversationId
     * @param message
     */
    /*@Override
    public void add(String conversationId, Message message) {
        // 调用saveConversation()方法，把单条消息当列表传进去，列表只有一条消息
        saveConversation(conversationId, List.of(message));
    }*/


    /**
     * 增 - 往指定对话插入多条消息
     * @param conversationId
     * @param messages
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        // 调用getOrCreateConversation()方法，获取往期消息
        List<Message> messageList = getOrCreateConversation(conversationId);
        messageList.addAll(messages);
        saveConversation(conversationId, messageList);
    }


    /**
     * 查 - 查询单条消息
     * @param lastN
     * @return
     */
    @Override
    public List<Message> get(String conversationId, int lastN) {
        // 从文件中获取所有的消息
        List<Message> messageList = getOrCreateConversation(conversationId);
        return messageList.stream()
                .skip(Math.max(0, messageList.size() - lastN))
                .toList();
    }


    /**
     * 删 - 删除单条消息
     * @param conversationId
     */
    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }


    /**
     * 获取或创建会话消息的列表
     * @param conversationId
     * @return
     */
    private List<Message> getOrCreateConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))){
                messages = kryo.readObject(input, ArrayList.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }


    /**
     * 保存会话信息
     * @param conversationId
     * @param messages
     */
    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))){
            kryo.writeObject(output, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 通用方法：获取当前会话的文件，保证每个会话文件单独保存
     * @param conversationId
     * @return
     */
    public File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }
}
