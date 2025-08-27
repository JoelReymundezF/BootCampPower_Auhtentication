//package co.com.crediya.usecase.role;
//
//import co.com.crediya.model.role.Role;
//import co.com.crediya.model.role.gateways.RoleRepository;
//import co.com.crediya.model.user.User;
//import co.com.crediya.model.user.gateways.UserRepository;
//import lombok.RequiredArgsConstructor;
//import reactor.core.publisher.Mono;
//
//@RequiredArgsConstructor
//public class RoleUseCase {
//
//    private final RoleRepository roleRepository;
//
//    public Mono<Role> saveRole(Role role){
//        return roleRepository.save(role);
//    }
//
//    public Mono<Role> findById(Long id){
//        return roleRepository.findById(id);
//    }
//}
