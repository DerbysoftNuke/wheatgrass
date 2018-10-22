package com.derby.nuke.wheatgrass

import com.google.common.collect.Sets
import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import java.time.LocalDate

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Configurable
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import com.derby.nuke.wheatgrass.entity.Sex
import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.service.BirthdayService
import com.derby.nuke.wheatgrass.wechat.service.UserDownloadService
import com.derby.nuke.wheatgrass.wechat.service.WechatService

@Component
@Configurable
@EnableScheduling
class ScheduleConfiguration implements UserDownloadService {

    private Logger logger = LoggerFactory.getLogger(ScheduleConfiguration)

    @Autowired
    UserRepository userRepository;
    @Autowired
    WechatService wechatService;
    @Autowired
    BirthdayService birthdayService;

    @Scheduled(cron = '${download.users.cron}')
    @Override
    void downloadUsers() {
        try {
            def userList = wechatService.getUsersByDepartment("1");
            def userIds = Sets.newHashSet();
            userList.each { item ->
                try {
                    userIds.add(item.userid);
                    downloadUser(item.userid);
                } catch (e) {
                    logger.error("Download user failed by ${item.userid}", e)
                }
            }
            userRepository.findAll().each { user ->
                if (!userIds.contains(user.userId)) {
                    logger.info(user.userId+"/"+user.name+ " is deleted")
                    user.birthday = null;
                    user.isDel = true
                    userRepository.saveAndFlush(user);
//                    userRepository.delete(user)
                }
            }
        } catch (ex) {
            logger.error("Download users failed", ex);
        } finally {
            logger.info("Done of downloading users");
        }

    }

    @Scheduled(cron = '${notice.happy.birthday.cron}')
    void happyBirthday() {
        try {
            birthdayService.happyBirthday(LocalDate.now());
        } catch (e) {
            logger.error("happyBirthday failed", e);
        } finally {
            logger.info("Done of happyBirthday");
        }
    }

    @Scheduled(cron = '${announce.birthday.cron}')
    void announceBirthdayPersons() {
        try {
            birthdayService.announceBirthdayPersons(LocalDate.now().plusMonths(1));
        } catch (e) {
            logger.error("announceBirthdayPersons failed", e);
        } finally {
            logger.info("Done of announceBirthdayPersons");
        }
    }

    @Override
    void downloadUser(userId) {
        logger.info("Downloading user {}", userId);
        def userInfo = wechatService.getUserInfo(userId);
        def user = userRepository.getByUserId(userId);
        if (user == null) {
            user = new User();
        }
        user.userId = userInfo.userid;
        user.name = userInfo.name;
        user.email = userInfo.email;
        user.nickName = userInfo.weixinid;
        user.position = userInfo.position;
        user.englishName = userInfo.english_name ?: ""
        if (userInfo.gender == "1") {
            user.sex = Sex.Male;
        } else if (userInfo.gender == "2") {
            user.sex = Sex.Female;
        }
        user.imageUrl = userInfo.avatar;
        userInfo.extattr.attrs.each { attr ->
            if (attr.name == "生日" && attr.value != null && attr.value != "") {
                try {
                    user.birthday = LocalDate.parse(attr.value);
                } catch (e) {
                    user.birthday = null;
                }
            } else if (attr.name == "籍贯") {
                user.birthplace = attr.value;
            } else if (attr.name == "入职日期" && attr.value != null && attr.value != "") {
                try {
                    user.entryday = LocalDate.parse(attr.value);
                } catch (e) {
                    user.entryday = null;
                }
            }
        }
        if (userInfo.department != null && userInfo.department.size() > 0) {
            def department = wechatService.getDepartment(userInfo.department[0]);
            user.department = department.name;
        }

        user.isDel = false
        userRepository.saveAndFlush(user);
    }
}
