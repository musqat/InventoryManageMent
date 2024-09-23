package com.zerobase.ims.inventory.repository;

import com.zerobase.ims.common.type.InviteStatus;
import com.zerobase.ims.inventory.entity.Invite;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteRepository extends JpaRepository<Invite, Long> {
  List<Invite> findByInviteIdAndStatus(Long inviteId, InviteStatus status);

}
