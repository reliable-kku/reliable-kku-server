package com.deundeunhaku.reliablekkuserver.menu.service;


import com.deundeunhaku.reliablekkuserver.menu.domain.Menu;
import com.deundeunhaku.reliablekkuserver.menu.dto.CreateMenuRequest;
import com.deundeunhaku.reliablekkuserver.menu.dto.CreateMenuResponse;
import com.deundeunhaku.reliablekkuserver.menu.repository.AdminMenuRepository;
import com.deundeunhaku.reliablekkuserver.s3.dto.S3Response;
import com.deundeunhaku.reliablekkuserver.s3.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class AdminCreateMenuService {

    private final AdminMenuRepository adminMenuRepository;
    private final S3UploadService uploadService;

    @Transactional
    public CreateMenuResponse creatAndCreateFile(MultipartFile multipartFile, CreateMenuRequest request){
            S3Response s3Response = uploadService.saveFileWithUUID(multipartFile);
            Menu menu = Menu.builder()
                    .name(request.name())
                    .description(request.description())
                    .pricePerOne(request.price())
                    .pricePerThree(request.price()*3)
                    .menuImageUrl(s3Response.s3ImageUrl())
                    .build();

        Menu savedMenu = adminMenuRepository.save(menu);
        return CreateMenuResponse.of(savedMenu.getId());
        }
    }
