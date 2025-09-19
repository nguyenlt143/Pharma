package vn.edu.fpt.pharma.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.edu.fpt.pharma.base.BaseServiceImpl;
import vn.edu.fpt.pharma.config.CustomUserDetails;
import vn.edu.fpt.pharma.dto.DataTableRequest;
import vn.edu.fpt.pharma.dto.DataTableResponse;
import vn.edu.fpt.pharma.dto.user.UserVM;
import vn.edu.fpt.pharma.entity.Store;
import vn.edu.fpt.pharma.entity.User;
import vn.edu.fpt.pharma.repository.StoreRepository;
import vn.edu.fpt.pharma.repository.UserRepository;
import vn.edu.fpt.pharma.service.AuditService;
import vn.edu.fpt.pharma.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long, UserRepository> implements UserService, UserDetailsService {

    private final StoreRepository storeRepository;

    public UserServiceImpl(UserRepository repository, AuditService auditService, StoreRepository storeRepository) {
        super(repository, auditService);
        this.storeRepository = storeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmailIgnoreCase(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUserDetails(user);
    }

    @Override
    public DataTableResponse<User> getAllUsers(DataTableRequest reqDto) {
        DataTableResponse<User> users = findAllForDataTable(reqDto, List.of("email", "fullName", "storeCode"));
        return users.transform(auditService::addAuditInfo);
    }

    public List<UserVM> transformUsers(List<User> users) {
        List<String> storeCodes = users.stream().map(User::getStoreCode).toList();
        List<Store> stores = storeRepository.findAllByStoreCodeIn(storeCodes);
        Map<String, String> storeNameMap = stores.stream()
                .collect(Collectors.toMap(Store::getStoreCode, Store::getStoreName, (s1, s2) -> s1));
        return users.stream().map(user -> new UserVM(user, storeNameMap.get(user.getStoreCode()))).toList();
    }
}
