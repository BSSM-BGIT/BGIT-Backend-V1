> 친구와 사이드 프로젝트로 진행한 프로젝트입니다. 재학생들의 동기부여와 학습 능력 증진에 도움이 될 것 같아 만들게 된 서비스입니다. BGIT은 부산소프트웨어마이스터고등학교 재학생들만 사용할 수 있으며, 자신의 깃허브와 백준 랭킹을 등록할 수 있습니다. 또한 커뮤니티 기능이 있어 유저끼리 자유롭게 소통도 할 수 있습니다. + 현재 코틀린과 스프링으로 버전2를 개발하고 있습니다.

### 팀 구성
Frontend 1명, Backend 1명, Android 1명

### Test ID/PW (View 전용)
ID: TestAccount <br>
PW: 12345678

### 담당한 기능
- BSM/Github Oauth를 통한 인증/인가 개발
- 인증된 정보를 바탕으로 유저의 깃허브 정보 저장 및 관리
- 사용자 인증/인가 구현과 토큰 관리
- 지속적인 랭킹 업데이트를 위한 스케쥴링 설정
- 백준 인증 개발
- 커뮤니티 기능 개발
- 신고하기 기능 개발

## 💡 개발하면서 만난 문제점과 해결에 대해서

- **이때까지 사용자로써만 경험해본 Oauth에 대한 이해**<br>
저는 여태 서비스를 이용하는 유저로써만 소셜로그인 등을 사용해봤습니다. 그래서 그런지 처음 Oauth를 이용하여 개발할 때 이론적인 지식이 부족해서 각 토큰이 어떤 역할들인지, 서버와 서버 사이 인증 방식이 어떻게 동작하는지 모르는 상태로 시작했었습니다.
    
    당연하게도 너무나 많은 이슈가 생겼고 다시 이론부터 천천히 정리한 뒤, 원인을 찾고 해결했습니다. 이번 기회에 Oauth가 동작하는 구조와 원리를 자세히 알고 적용할 수 있게 되었습니다. 백엔드 동작 방식과 원리가 매우 중요하다는 것을 깨달았습니다.
    

- **지속적인 유저 정보 업데이트**<br>
이 서비스는 유저가 실시간으로 자신의 랭킹을 확인하기 위해서 지속적인 정보 업데이트가 필요하다고 생각했습니다.
    
    하지만 실시간으로 많은 유저들의 정보를 타 서버에 요청해서 유저의 정보를 수정한 후 다시 반환해주는 방식은 서버끼리 요청, 응답하는 시간이 끼어있기에 사용자가 로딩 화면을 보게 되는 시간이 눈에 띄게 늘어날 것이라고 생각했습니다. 
    
    그래서 사용자들이 많이 접속하지 않는 새벽 시간대에 유저 정보를 업데이트하는 스케쥴러를 배치해서 지속적인 유저 정보 업데이트라는 이슈를 해결했습니다.
    
- **EC2 인스턴스의 스펙**<br>
백엔드 개발을 마친 후 배포를 진행할 때 백그라운드로 jar를 실행했는데도 불구하고 네트워크 대역폭이 매우 작아지고 서버가 자꾸 죽는 상황이 발생했었습니다.
    
    평소 프리티어로도 잘 사용하고있는 ec2였기에 별 생각없이 다른 방향으로 해결하려고 했습니다. 하지만 계속 문제점을 찾다보니 모든 유저를 업데이트하는 스케쥴러 시간대에 Out Of Memory가 발생했었고, 스왑 메모리 기법을 사용하여 해결했습니다. 
    
    이번기회에 백엔드 개발자가 왜 백엔드만 알아서는 안되는지, 인프라에서는 어떤 요소들을 고려해야하는지 다시 한번 생각하게 되었습니다.
    
- **백준 인증에 대하여**<br>
깃허브 뿐만 아니라 백준에서 사용자의 재미와 현재 수준을 보기 쉽도록 해주는 Solved.ac라는 랭킹 기능을 가져와야 했습니다.
    
    하지만 백준에서 Oauth를 제공해주지는 않았기에 “어떻게하면 백준 아이디가 사용자 아이디인 것을 증명해서 인증할 수 있을까?” 라는 고민을 무척 많이 했습니다. 
    
    하지만 인증이라는 개념을 너무 좁은 시야로 봤던 것은 아닌지 Oauth가 아니면 타 서비스에서의 인증이 불가능하다고 생각했습니다. 
    
    결국 찾은 정답은 “Oauth가 아닌 Solved.ac에서 제공해주는 API를 활용하자” 였습니다. Solved.ac는 사용자의 랭킹과 더불어 상태메시지에 관련된 내용도 제공해주었고, 유저가 백준 아이디를 입력하면 서버가 랜덤한 인증코드를 발급하고 유저가 코드를 상태메시지에 적용하고 확인 버튼을 누르면 인증을 하는 방식으로 인증 부분을 개발하였습니다. 
    
    이 이슈로 인해서 너무나 큰 개념을 나 자신이 너무 제한된 시점에서 바라보는 것이 아니었나.. 하는 생각이 많이 들었습니다.
