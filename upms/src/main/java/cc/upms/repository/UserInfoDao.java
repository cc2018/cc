package cc.upms.repository;

import cc.upms.domain.UserInfo;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserInfoDao extends PagingAndSortingRepository<UserInfo,Long> {
    /**
     * 根据userName获取UserInfo
     * @param userName
     * @return
     */
    public UserInfo findByUserName(String userName);

    /**
     * 根据userId获取UserInfo
     * @param userId
     * @return
     */
    public UserInfo findByUserId(Long userId);
}
