package net.qiujuer.web.italker.push.factory;

import com.google.common.base.Strings;
import net.qiujuer.web.italker.push.bean.api.base.PushModel;
import net.qiujuer.web.italker.push.bean.card.GroupMemberCard;
import net.qiujuer.web.italker.push.bean.card.MessageCard;
import net.qiujuer.web.italker.push.bean.card.UserCard;
import net.qiujuer.web.italker.push.bean.db.*;
import net.qiujuer.web.italker.push.utils.Hib;
import net.qiujuer.web.italker.push.utils.PushDispatcher;
import net.qiujuer.web.italker.push.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class PushFactory {

    public static void pushNewMessage(User sender, Message message) {
        if (sender == null || message == null)
            return;

        MessageCard card = new MessageCard(message);

        String entity = TextUtil.toJson(card);

        PushDispatcher dispatcher = new PushDispatcher();

        if (message.getGroup() == null
                && Strings.isNullOrEmpty(message.getGroupId())) {

            User receiver = UserFactory.findById(message.getReceiverId());
            if (receiver == null)
                return;

            PushHistory history = new PushHistory();

            history.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());


            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(), history.getEntity());

            dispatcher.add(receiver, pushModel);

            Hib.queryOnly(session -> session.save(history));
        } else {

            Group group = message.getGroup();
            if (group == null)
                group = GroupFactory.findById(message.getGroupId());

            if (group == null)
                return;

            Set<GroupMember> members = GroupFactory.getMembers(group);
            if (members == null || members.size() == 0)
                return;

            members = members.stream()
                    .filter(groupMember -> !groupMember.getUserId()
                            .equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());
            if (members.size() == 0)
                return;

            List<PushHistory> histories = new ArrayList<>();

            addGroupMembersPushModel(dispatcher,
                    histories,
                    members,
                    entity,
                    PushModel.ENTITY_TYPE_MESSAGE);

            Hib.queryOnly(session -> {
                for (PushHistory history : histories) {
                    session.saveOrUpdate(history);
                }
            });
        }

        dispatcher.submit();

    }

    private static void addGroupMembersPushModel(PushDispatcher dispatcher,
                                                 List<PushHistory> histories,
                                                 Set<GroupMember> members,
                                                 String entity,
                                                 int entityTypeMessage) {
        for (GroupMember member : members) {
            User receiver = member.getUser();
            if (receiver == null)
                return;

            PushHistory history = new PushHistory();
            history.setEntityType(entityTypeMessage);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);


            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(), history.getEntity());

            dispatcher.add(receiver, pushModel);
        }
    }

    public static void pushJoinGroup(Set<GroupMember> members) {

        PushDispatcher dispatcher = new PushDispatcher();

        List<PushHistory> histories = new ArrayList<>();

        for (GroupMember member : members) {
            User receiver = member.getUser();
            if (receiver == null)
                return;

            GroupMemberCard memberCard = new GroupMemberCard(member);
            String entity = TextUtil.toJson(memberCard);

            PushHistory history = new PushHistory();
            history.setEntityType(PushModel.ENTITY_TYPE_ADD_GROUP);
            history.setEntity(entity);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);


            PushModel pushModel = new PushModel()
                    .add(history.getEntityType(), history.getEntity());


            dispatcher.add(receiver, pushModel);
            histories.add(history);
        }


        Hib.queryOnly(session -> {
            for (PushHistory history : histories) {
                session.saveOrUpdate(history);
            }
        });


        dispatcher.submit();
    }


    public static void pushGroupMemberAdd(Set<GroupMember> oldMembers, List<GroupMemberCard> insertCards) {

        PushDispatcher dispatcher = new PushDispatcher();


        List<PushHistory> histories = new ArrayList<>();


        String entity = TextUtil.toJson(insertCards);


        addGroupMembersPushModel(dispatcher, histories, oldMembers,
                entity, PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);


        Hib.queryOnly(session -> {
            for (PushHistory history : histories) {
                session.saveOrUpdate(history);
            }
        });

        dispatcher.submit();
    }


    public static void pushLogout(User receiver, String pushId) {

        PushHistory history = new PushHistory();

        history.setEntityType(PushModel.ENTITY_TYPE_LOGOUT);
        history.setEntity("Account logout!!!");
        history.setReceiver(receiver);
        history.setReceiverPushId(pushId);

        Hib.queryOnly(session -> session.save(history));


        PushDispatcher dispatcher = new PushDispatcher();

        PushModel pushModel = new PushModel()
                .add(history.getEntityType(), history.getEntity());


        dispatcher.add(receiver, pushModel);
        dispatcher.submit();
    }


    public static void pushFollow(User receiver, UserCard userCard) {

        userCard.setFollow(true);
        String entity = TextUtil.toJson(userCard);


        PushHistory history = new PushHistory();

        history.setEntityType(PushModel.ENTITY_TYPE_ADD_FRIEND);
        history.setEntity(entity);
        history.setReceiver(receiver);
        history.setReceiverPushId(receiver.getPushId());

        Hib.queryOnly(session -> session.save(history));


        PushDispatcher dispatcher = new PushDispatcher();
        PushModel pushModel = new PushModel()
                .add(history.getEntityType(), history.getEntity());
        dispatcher.add(receiver, pushModel);
        dispatcher.submit();
    }
}
